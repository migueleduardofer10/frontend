import { Component, OnInit } from '@angular/core';
import { UserService } from '../_services/user.service';
import { User } from '../_model/user.model';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  message;
  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.forUser();
    this.getCurrentUser();
  }

  forUser() {
    this.userService.forUser().subscribe(
      (response) => {
        console.log(response);
        this.message = response;
      }, 
      (error)=>{
        console.log(error);
      }
    );
  }

  //obtener el nombre del usuario
  getCurrentUser() {
    this.userService.getCurrentUser().subscribe(
      (response: User) => {
        // Imprimir solo el nombre del usuario
        this.message = response.userName;
      }, 
      (error) => {
        console.log(error);
      }
    );
  }
  

  
}
