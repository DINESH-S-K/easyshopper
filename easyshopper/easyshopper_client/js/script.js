document.addEventListener('DOMContentLoaded', function () {
  const loginBtn = document.getElementById('loginBtn');
  const signupBtn = document.getElementById('signupBtn');
  // Add click event listeners to the buttons
  loginBtn.addEventListener('click', handleLogin);
  signupBtn.addEventListener('click', handleSignup);
  function handleLogin() {
    window.location.href = 'login.html';
  }
  function handleSignup() {
    window.location.href = 'signup.html';
  }
});
