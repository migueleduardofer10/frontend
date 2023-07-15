import { ChangeDetectorRef, Component, Injector, NgZone, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
// import * as Razorpay from 'razorpay';
import { OrderDetails } from '../_model/order-details.model';
import { Product } from '../_model/product.model';
import { ProductService } from '../_services/product.service';
import { UserService } from '../_services/user.service';
import { User } from '../_model/user.model';

declare var paypal: any;

@Component({
  selector: 'app-buy-product',
  templateUrl: './buy-product.component.html',
  styleUrls: ['./buy-product.component.css']
})
export class BuyProductComponent implements OnInit {
  userName: string;

  isSingleProductCheckout: boolean = false;
  productDetails: Product[] = [];

  orderDetails: OrderDetails = {
    fullName: '',
    fullAddress: '',
    contactNumber: '',
    alternateContactNumber: '',
    transactionId: '',
    orderProductQuantityList: []
  };

  constructor(
    private activatedRoute: ActivatedRoute,
    private productService: ProductService,
    private userService: UserService,
    private router: Router,
    private injector: Injector
  ) {}

  ngOnInit(): void {
    this.productDetails = this.activatedRoute.snapshot.data['productDetails'];
    this.isSingleProductCheckout =
      this.activatedRoute.snapshot.paramMap.get('isSingleProductCheckout') === 'true';

    this.productDetails.forEach((x) =>
      this.orderDetails.orderProductQuantityList.push({ productId: x.productId, quantity: 1 })
    );

    console.log(this.productDetails);
    console.log(this.orderDetails);
    this.getCurrentUser();
  }

  public placeOrder(orderForm: NgForm) {
    console.log(orderForm.value);
    console.log(this.orderDetails);
    this.productService
      .placeOrder(this.userName, this.orderDetails, this.isSingleProductCheckout)
      .subscribe(
        (response) => {
          console.log(response);
          this.router.navigate(["/orderConfirm"]);
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
      }, 
      (error) => {
        console.log(error);
      }
    );
  }

  getQuantityForProduct(productId) {
    const filteredProduct = this.orderDetails.orderProductQuantityList.filter(
      (productQuantity) => productQuantity.productId === productId
    );

    return filteredProduct[0].quantity;
  }

  getCalculatedTotal(productId, productDiscountedPrice) {
    const filteredProduct = this.orderDetails.orderProductQuantityList.filter(
      (productQuantity) => productQuantity.productId === productId
    );

    return filteredProduct[0].quantity * productDiscountedPrice;
  }

  onQuantityChanged(q, productId) {
    this.orderDetails.orderProductQuantityList.filter(
      (orderProduct) => orderProduct.productId === productId
    )[0].quantity = q;
  }

  getCalculatedGrandTotal() {
    let grandTotal = 0;

    this.orderDetails.orderProductQuantityList.forEach((productQuantity) => {
      const price = this.productDetails.find((product) => product.productId === productQuantity.productId)
        .productDiscountedPrice;
      grandTotal += price * productQuantity.quantity;
    });

    return grandTotal;
  }

  createTransactionAndPlaceOrder(orderForm: NgForm) {
    let amount = this.getCalculatedGrandTotal();
    this.productService.createTransaction(amount).subscribe(
      (response) => {
        console.log(response);
        this.openTransactionModal(response, orderForm);
      },
      (error) => {
        console.log(error);
      }
    );
  }

  openTransactionModal(response: any, orderForm: NgForm) {
    const totalAmount = this.getCalculatedGrandTotal();
    paypal.Buttons({
      createOrder: (data, actions) => {
        return actions.order.create({
          purchase_units: [
            {
              amount: {
                value: totalAmount,
                currency: 'USD' // Use your desired currency
              }
            }
          ]
        });
      },
      onApprove: (data, actions) => {
        return actions.order.capture().then((details) => {
          console.log(details);
          this.orderDetails.transactionId = details.id;
          this.placeOrder(orderForm);
        });
      },
      onError: (err) => {
        console.log(err);
        alert('Payment failed..');
      }
    }).render('#paypal-button-container');
  }

  processResponse(resp: any, orderForm: NgForm) {
    this.orderDetails.transactionId = resp.razorpay_payment_id;
    this.placeOrder(orderForm);
  }
}
