import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { OrderDetails } from '../_model/order-details.model';
import { MyOrderDetails } from '../_model/order.model';
import { Product } from '../_model/product.model';

@Injectable({
  providedIn: 'root'
})

export class ProductService {

  constructor(private httpClient: HttpClient) { }

  public createTransaction(amount) {
    return this.httpClient.get("http://localhost:8180/transactions/" + amount);
  }

  public markAsDelivered(orderId) {
    return this.httpClient.put(`http://localhost:8081/orders/${orderId}/deliver`, {});
  }

  public getAllOrderDetailsForAdmin(status: string): Observable<MyOrderDetails[]> {
    return this.httpClient.get<MyOrderDetails[]>("http://localhost:8081/orders/status/" + status);
  }

  public getMyOrders(userName: string): Observable<MyOrderDetails[]> {
    return this.httpClient.get<MyOrderDetails[]>("http://localhost:8081/orders/" + userName);
  }

  public deleteCartItem(cartId) {
    return this.httpClient.delete("http://localhost:8888/carts/" + cartId);
  }

  public addProduct(product: FormData) {
    return this.httpClient.post<Product>("http://localhost:9090/products", product);
  }

  public getAllProducts(pageNumber, searchKeyword: string = "") {
    return this.httpClient.get<Product[]>("http://localhost:9090/products?pageNumber=" + pageNumber + "&searchKey=" + searchKeyword);
  }

  public getProductDetailsById(productId) {
    return this.httpClient.get<Product>("http://localhost:9090/products/" + productId);
  }

  public deleteProduct(productId: number) {
    return this.httpClient.delete("http://localhost:9090/products/" + productId);
  }

  public getProductDetails(isSingleProductCheckout, productId) {
    return this.httpClient.get<Product[]>("http://localhost:9090/products/" + isSingleProductCheckout + "/" + productId);
  }

  public placeOrder(userName: string, orderDetails: OrderDetails, isCartCheckout: boolean): Observable<void> {
    return this.httpClient.post<void>(`http://localhost:8081/orders/${userName}/${isCartCheckout}`, orderDetails);
}


  addToCart(userName: string, productId: number) {
    console.log(productId);
    console.log(userName); 
    const url = `http://localhost:8888/carts/${userName}/${productId}`;
    return this.httpClient.get(url);
  }

  
  

  //GET cART DETAILS Y ENVIA COMO PATH VARIABLE EL USERNAME
  public getCartDetails(userName: string) {
    console.log(userName);
    const url = `http://localhost:8888/carts/${userName}`;
    return this.httpClient.get(url);
  }
  
}
