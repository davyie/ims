import { Pipe, PipeTransform } from '@angular/core';
import { formatDistanceToNow, format, parseISO } from 'date-fns';

@Pipe({ name: 'dateFormat', standalone: true })
export class DateFormatPipe implements PipeTransform {
  transform(date: string | null | undefined, mode: 'relative' | 'absolute' = 'absolute'): string {
    if (!date) return '-';
    try {
      const parsed = parseISO(date);
      if (mode === 'relative') {
        return formatDistanceToNow(parsed, { addSuffix: true });
      }
      return format(parsed, 'dd MMM yyyy, HH:mm');
    } catch {
      return date;
    }
  }
}
