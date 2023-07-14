export interface User {
    userName: string;
    userFirstName: string;
    userLastName: string;
    userPassword: string;
    verificationCode: string | null;
    enabled: boolean;
    role: Role[];
  }

  export interface Role {
    roleName: string;
    roleDescription: string;
  }