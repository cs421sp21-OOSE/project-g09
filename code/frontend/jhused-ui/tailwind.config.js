// tailwind.config.js
module.exports = {
  purge: ["./src/**/*.{js,jsx,ts,tsx}", "./public/index.html"],
  darkMode: false, // or 'media' or 'class'
  theme: {
    extend: {},
    fontFamily: {
      sans: ['OpenSans','Open Sans', 'sans-serif']
    }
  },
  variants: {
    extend: {},
  },
  plugins: [],
};
