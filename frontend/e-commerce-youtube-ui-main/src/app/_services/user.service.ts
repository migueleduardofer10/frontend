import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserAuthService } from './user-auth.service';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  PATH_OF_API = 'http://localhost:8080';

  requestHeader = new HttpHeaders({ 'No-Auth': 'True' });
  constructor(
    private httpclient: HttpClient,
    private userAuthService: UserAuthService
  ) {}

  public register(registerData) {
    return this.httpclient.post(this.PATH_OF_API + '/users', registerData);
  }

  

  public getCurrentUser() {
    let token = this.userAuthService.getToken();
    console.log('Token:', token);  // Log the token
    let headers = new HttpHeaders().set("Authorization", "Bearer " + token);
    return this.httpclient.get(this.PATH_OF_API + '/me', { headers: headers });
  }
  





  public login(loginData) {
    return this.httpclient.post(this.PATH_OF_API + '/jwt/authenticate', loginData, {
      headers: this.requestHeader,
    });
  }

  public forUser() {
    return this.httpclient.get(this.PATH_OF_API + '/users/user', {
      responseType: 'text',
    });
  }

  public forAdmin() {
    return this.httpclient.get(this.PATH_OF_API + '/users/admin', {
      responseType: 'text',
    });
  }

  public roleMatch(allowedRoles): boolean {
    let isMatch = false;
    const userRoles: any = this.userAuthService.getRoles();

    if (userRoles != null && userRoles) {
      for (let i = 0; i < userRoles.length; i++) {
        for (let j = 0; j < allowedRoles.length; j++) {
          if (userRoles[i].roleName === allowedRoles[j]) {
            isMatch = true;
            return isMatch;
          } else {
            return isMatch;
          }
        }
      }
    }
  }
}
