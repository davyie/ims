import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { CatalogueComponent } from './catalogue/catalogue.component';
import { CatalogueFormComponent } from './catalogue-form/catalogue-form.component';

export const routes: Routes = [
    {path: "", component: HomeComponent},
    {path: "catalogue", component: CatalogueComponent},
    {path: "form", component: CatalogueFormComponent},
];
