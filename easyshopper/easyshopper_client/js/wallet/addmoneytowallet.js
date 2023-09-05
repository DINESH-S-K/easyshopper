import AuthUtility from "../authutility.js";

AuthUtility.userInfo().then(data => {
  console.log(data);
  if (!data && data.roleId !== 3) {
    alert("Access denied. You are not authorized to access this page.");
    window.location.href = 'login.html';
  }
});

document.getElementById('addMoneyButton').addEventListener('click', onAddMoneyButtonClicked);
document.getElementById('backButton').addEventListener('click', onBackButtonClicked);

function onAddMoneyButtonClicked() {
  const pin = document.getElementById('pin').value;
  const amount = parseFloat(document.getElementById('amount').value);
  
  fetch(`http://localhost:8080/easyshopper_server2/addMoneyToWallet?pin=${pin}&amount=${amount}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    credentials:'include'
  })
  .then(response => {
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
  })
  .then(data => {
    alert('Money added to wallet successfully.');
    window.history.back();
  })
  .catch(error => {
    console.error('Error:', error);
    alert('Error occurred while adding money to the wallet.');
  });
  
}

function onBackButtonClicked() {
  window.history.back(); // Go back to the previous page
}
