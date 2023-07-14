import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ProductService } from '../_services/product.service';
import { UserService } from '../_services/user.service';
import { User } from '../_model/user.model';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  userName: string;

  displayedColumns: string[] = ['Name', 'Description', 'Price', 'Discounted Price', 'Action'];

  cartDetails: any[] = [];

  constructor(
    private productService: ProductService,
    private userService: UserService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.getCurrentUser();
  }

  delete(cartId: any) {
    console.log(cartId);
    this.productService.deleteCartItem(cartId).subscribe(
      (resp) => {
        console.log(resp);
        this.getCartDetails(this.userName);
      },
      (err) => {
        console.log(err);
      }
    );
  }

  getCartDetails(userName: string) {
    console.log(userName);
    this.productService.getCartDetails(userName).subscribe(
      (response: any[]) => {
        console.log(response);
        this.cartDetails = response; // Asignar la respuesta a cartDetails
      },
      (error) => {
        console.log(error);
      }
    );
}

  getCurrentUser() {
    this.userService.getCurrentUser().subscribe(
      (response: User) => {
        // Imprimir solo el nombre del usuario
        this.userName = response.userName;
        console.log(this.userName);
        this.getCartDetails(this.userName); // Llamar a getCartDetails() después de obtener el nombre de usuario
      },
      (error) => {
        console.log(error);
      }
    );
  }

  checkout() {
    this.router.navigate(['/buyProduct'], {
      queryParams: {
        isSingleProductCheckout: false,
        id: 0
      }
    });
  }
}
