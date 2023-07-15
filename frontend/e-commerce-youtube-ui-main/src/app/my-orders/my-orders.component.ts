import { Component, OnInit } from '@angular/core';
import { MyOrderDetails } from '../_model/order.model';
import { ProductService } from '../_services/product.service';
import { UserService } from '../_services/user.service';
import { User } from '../_model/user.model';

@Component({
  selector: 'app-my-orders',
  templateUrl: './my-orders.component.html',
  styleUrls: ['./my-orders.component.css']
})
export class MyOrdersComponent implements OnInit {
  userName: string;

  displayedColumns = ["Name", "Address", "Contact No.", "Amount", "Status"];

  myOrderDetails: MyOrderDetails[] = [];

  constructor(private productService: ProductService, private userService: UserService
    ) { }

  ngOnInit(): void {
    this.getCurrentUser();
  }

  getOrderDetails( userName: string) {
    this.productService.getMyOrders(this.userName).subscribe(
      (resp: MyOrderDetails[]) => {
        console.log(resp);
        this.myOrderDetails = resp;
      }, (err)=> {
        console.log(err);
      }
    );
  }

  getCurrentUser() {
    this.userService.getCurrentUser().subscribe(
      (response: User) => {
        // Imprimir solo el nombre del usuario
        this.userName = response.userName;
        console.log(this.userName);
        this.getOrderDetails(this.userName);
      },
      (error) => {
        console.log(error);
      }
    );
  }

}
