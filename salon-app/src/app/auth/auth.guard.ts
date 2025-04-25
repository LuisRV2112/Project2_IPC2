import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const token = localStorage.getItem('auth_token');
    
    const url = state.url.split('?')[0];
    const publicRoutes = ['/login', '/register'];
    
    if (token && publicRoutes.includes(url)) {
      this.router.navigate(['/bienvenida']);
      return false;
    }
    
    if (!token && !publicRoutes.includes(url)) {
      this.router.navigate(['/login']);
      return false;
    }
    
    return true;
  }
}