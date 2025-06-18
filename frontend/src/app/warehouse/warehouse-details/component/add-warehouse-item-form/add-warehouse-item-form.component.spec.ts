import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddWarehouseItemFormComponent } from './add-warehouse-item-form.component';

describe('AddWarehouseItemFormComponent', () => {
  let component: AddWarehouseItemFormComponent;
  let fixture: ComponentFixture<AddWarehouseItemFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddWarehouseItemFormComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AddWarehouseItemFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
