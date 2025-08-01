import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { CatalogueComponent } from './catalogue/catalogue.component';
import { WarehouseComponent } from './warehouse/warehouse.component';
import { MarketComponent } from './market/market.component';
import { WarehouseDetailsComponent } from './warehouse/warehouse-details/warehouse-details.component';
import { MarketdetailsComponent } from './market/marketDetails/marketdetails.component';

export const routes: Routes = [
    {path: "", component: HomeComponent},
    {path: "catalogue", component: CatalogueComponent},
    {path: "warehouse", component: WarehouseComponent},
    {path: "warehouse/:id", component: WarehouseDetailsComponent},
    {path: "market", component: MarketComponent},
    {path: "market/:marketName", component: MarketdetailsComponent},
];
