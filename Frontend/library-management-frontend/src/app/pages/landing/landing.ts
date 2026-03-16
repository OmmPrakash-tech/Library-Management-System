import { Component } from '@angular/core';

import { Navbar } from '../../components/landing/navbar/navbar';
import { Hero } from '../../components/landing/hero/hero';
import { Feature } from '../../components/landing/feature/feature';
import { Pricing } from '../../components/landing/pricing/pricing';
import { Testimonials } from '../../components/landing/testimonials/testimonials';
import { Faq } from '../../components/landing/faq/faq';
import { Demo } from '../../components/landing/demo/demo';
import { Footer } from '../../components/landing/footer/footer';

@Component({
  selector: 'app-landing',
  imports: [
    Navbar,
    Hero,
    Feature,
    Pricing,
    Testimonials,
    Faq,
    Demo,
    Footer
  ],
  templateUrl: './landing.html',
  styleUrl: './landing.css'
})
export class Landing {}