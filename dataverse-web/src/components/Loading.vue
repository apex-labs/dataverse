<script setup lang="ts" name="loading">
</script>

<template>
  <div class="app-loading">
    <div class="space"></div>
    <svg class="editorial" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"
      viewBox="0 24 150 28" preserveAspectRatio="none">
      <defs>
        <path id="gentle-wave" d="M-160 44c30 0 
    58-18 88-18s
    58 18 88 18 
    58-18 88-18 
    58 18 88 18
    v44h-352z" />
      </defs>
      <g class="parallax">
        <use xlink:href="#gentle-wave" x="50" y="0" fill="#473793" />
        <use xlink:href="#gentle-wave" x="50" y="3" fill="#3461c1" />
        <use xlink:href="#gentle-wave" x="50" y="6" fill="#3C6DF0" />
      </g>
    </svg>
    <div class="content">
      <h1 contenteditable="true">NEXUS DATAVS</h1>
    </div>

    <link href="https://fonts.googleapis.com/css?family=Lato:100" rel="stylesheet" />
  </div>
</template>

<style>
html[data-theme='dark'] .app-loading {
  background-color: #2c344a;
}

::selection {
  background-color: salmon;
  color: white;
}

.parallax>use {
  animation: move-forever 12s linear infinite;

  &:nth-child(1) {
    animation-delay: -2s;
  }

  &:nth-child(2) {
    animation-delay: -2s;
    animation-duration: 5s;
  }

  &:nth-child(3) {
    animation-delay: -4s;
    animation-duration: 3s;
  }
}

@keyframes move-forever {
  0% {
    transform: translate(-90px, 0%);
  }

  100% {
    transform: translate(85px, 0%);
  }
}

.editorial {
  display: block;
  width: 100%;
  height: 15vh;
  margin: 0;
}

.app-loading {
  font-size: 14px;
  background-color: #000;
  margin: 0;
  height: 100vh;
  overflow: hidden;
  padding: 0;
  display: flex;
  flex-direction: column;
}

.space {
  flex: 1;
}

.content {
  font-family: 'Lato', sans-serif;
  text-align: center;
  background-color: #3c6df0;
  text-align: center;
  height: 30vh;
  padding: 1em;
  color: #eee;
  font-size: 2em;
  font-weight: 300;
}

.content {
  font-size: 1em;
}

.content h1 {
  transform: translateY(-1em);
  font-weight: 100;
  font-size: 3em;
  line-height: 1.4;
  user-select: none;
  cursor: default;
  font-family: unset;
}

/*prevent many large-by-comparison ripples by shrinking the height*/
@media (max-width: 50em) {
  .content h1 {
    font-size: 10vmax;
  }
}

h1 {
  outline: none;
}
</style>