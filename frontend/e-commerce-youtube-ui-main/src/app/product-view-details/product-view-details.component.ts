import { User } from './../_model/user.model';
import { UserService } from './../_services/user.service';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Product } from '../_model/product.model';
import { ProductService } from '../_services/product.service';

@Component({
  selector: 'app-product-view-details',
  templateUrl: './product-view-details.component.html',
  styleUrls: ['./product-view-details.component.css']
})
export class ProductViewDetailsComponent implements OnInit {

  selectedProductIndex = 0;

  product: Product;
  //Define una variable para almacenar el userName
  userName: string;

  constructor(private activatedRoute: ActivatedRoute,
    private router: Router,
    private UserService: UserService,
    private productService: ProductService) { }

  ngOnInit(): void {
    this.product = this.activatedRoute.snapshot.data['product'];
    console.log(this.product)
  }

  addToCart(userName, productId) {
    this.getCurrentUser();
    console.log(this.userName); 
    this.productService.addToCart(userName, productId).subscribe(
      (response) => {
        console.log(response);
      }, (error)=> {
        console.log(error);
      }
    );
  }

  getCurrentUser() {
    this.UserService.getCurrentUser().subscribe(
      (response: User) => {
        // Imprimir solo el nombre del usuario
        this.userName = response.userName;
        console.log(this.userName); 
      }, 
      (error) => {
        console.log(error);
      }
    );
  }

  changeIndex(index) {
    this.selectedProductIndex = index;
  }

  buyProduct(productId) {
    this.router.navigate(['/buyProduct', {
      isSingleProductCheckout: true, id: productId
    }]);
  }
}
