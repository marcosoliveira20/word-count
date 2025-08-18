import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-placeholder',
  standalone: true,
  template: `
    <div class="page">
      <h2 style="margin-top:0;">Em breve</h2>
      <p>Esta área será implementada em uma próxima entrega.</p>
    </div>
  `
})
export default class PlaceholderComponent {}
