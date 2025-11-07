const slides = document.querySelector('.slides');
const slideImages = document.querySelectorAll('.slides a');
const prevBtn = document.querySelector('.prev');
const nextBtn = document.querySelector('.next');

let index = 0;
const total = slideImages.length;

function showSlide(i) {
  if (i >= total) index = 0;
  else if (i < 0) index = total - 1;
  else index = i;

  const slideWidth = slideImages[0].clientWidth;
  slides.style.transform = `translateX(${-slideWidth * index}px)`;
}

nextBtn.addEventListener('click', () => showSlide(index + 1));
prevBtn.addEventListener('click', () => showSlide(index - 1));

setInterval(() => showSlide(index + 1), 5000);

window.addEventListener('resize', () => showSlide(index));

showSlide(index);
