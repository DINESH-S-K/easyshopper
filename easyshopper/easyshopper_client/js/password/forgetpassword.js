document.getElementById('verifyButton').addEventListener('click', function (event) {
  event.preventDefault();

  const username = document.getElementById('username').value;
  const email = document.getElementById('email').value;
  const url = `http://localhost:8080/easyshopper_server2/forgetPassword?username=${username}&email=${email}`;

  fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    }
  })
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then(data => {
      if (data.verify === true) {
        window.location.href = 'forgetandchangepassword.html';
      } else {
        alert('Username and email do not match.');
      }
    })
    .catch(error => {
      console.error('Error:', error);
    });
});

