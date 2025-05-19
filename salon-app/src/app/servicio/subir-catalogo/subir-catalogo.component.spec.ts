import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubirCatalogoComponent } from './subir-catalogo.component';

describe('SubirCatalogoComponent', () => {
  let component: SubirCatalogoComponent;
  let fixture: ComponentFixture<SubirCatalogoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubirCatalogoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SubirCatalogoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
