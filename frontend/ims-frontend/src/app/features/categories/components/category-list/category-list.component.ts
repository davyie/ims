import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog';
import { CategoryStateService } from '../../services/category-state.service';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header.component';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import { Category } from '../../../../shared/models/models';

@Component({
  selector: 'app-category-list',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatIconModule, MatListModule, MatTooltipModule,
    PageHeaderComponent, ConfirmDialogComponent
  ],
  templateUrl: './category-list.component.html',
  styleUrls: ['./category-list.component.scss']
})
export class CategoryListComponent implements OnInit {
  state = inject(CategoryStateService);
  private fb = inject(FormBuilder);
  private dialog = inject(MatDialog);

  saving = signal(false);

  form = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]]
  });

  ngOnInit(): void {
    this.state.loadCategories();
  }

  async add(): Promise<void> {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    try {
      await this.state.createCategory({ name: this.form.getRawValue().name! });
      this.form.reset();
    } finally {
      this.saving.set(false);
    }
  }

  async delete(category: Category): Promise<void> {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Delete Category',
        message: `Delete category "${category.name}"? Items using this category will keep the name but it won't appear in the list.`,
        confirmLabel: 'Delete',
        confirmColor: 'warn'
      }
    });
    const confirmed = await ref.afterClosed().toPromise();
    if (confirmed) await this.state.deleteCategory(category.id);
  }
}
