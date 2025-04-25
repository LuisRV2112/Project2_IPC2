import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdsService, Ad } from '../ads.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-anuncios-cliente',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './anuncios-cliente.component.html',
  styleUrls: ['./anuncios-cliente.component.scss']
})
export class AnunciosClienteComponent implements OnInit {
  anuncios: Ad[] = [];

  constructor(private adsService: AdsService, private sanitizer: DomSanitizer) {}

  ngOnInit(): void {
    this.adsService.obtenerAnuncios().subscribe({
      next: (res) => {
        console.log(res.data);
        const hoy = new Date().toISOString().split('T')[0];
        this.anuncios = res.data.filter(ad => ad.startDate <= hoy && ad.endDate >= hoy);
      },
      error: () => {
        console.error('Error al cargar anuncios del cliente');
      }
    });
  }

  esYoutube(url: string): boolean {
    return url.includes('youtube.com') || url.includes('youtu.be');
  }

  urlYoutubeEmbed(url: string): SafeResourceUrl {
    const id = this.extraerYoutubeId(url);
    return this.sanitizer.bypassSecurityTrustResourceUrl(`https://www.youtube.com/embed/${id}`);
  }

  private extraerYoutubeId(url: string): string {
    if (url.includes('youtu.be/')) return url.split('youtu.be/')[1];
    if (url.includes('watch?v=')) return url.split('v=')[1].split('&')[0];
    return '';
  }
  
}
