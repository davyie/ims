import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { CatalogueComponent } from './catalogue/catalogue.component';
import { CatalogueFormComponent } from './catalogue-form/catalogue-form.component';
import { WarehouseComponent } from './warehouse/warehouse.component';
import { MarketComponent } from './market/market.component';
import { WarehouseDetailsComponent } from './warehouse/warehouse-details/warehouse-details.component';

export const routes: Routes = [
    {path: "", component: HomeComponent},
    {path: "catalogue", component: CatalogueComponent},
    {path: "warehouse", component: WarehouseComponent},
    {path: "market", component: MarketComponent},
    {path: "warehouse/:id", component: WarehouseDetailsComponent},
];
