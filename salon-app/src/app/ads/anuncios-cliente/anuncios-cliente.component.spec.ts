import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnunciosClienteComponent } from './anuncios-cliente.component';

describe('AnunciosClienteComponent', () => {
  let component: AnunciosClienteComponent;
  let fixture: ComponentFixture<AnunciosClienteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnunciosClienteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AnunciosClienteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
