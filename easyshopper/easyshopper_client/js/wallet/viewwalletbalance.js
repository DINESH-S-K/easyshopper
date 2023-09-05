import AuthUtility from "../authutility.js";

AuthUtility.userInfo().then(data => {
  console.log(data);
  if (!data && data.roleId !== 3) {
    alert("Access denied. You are not authorized to access this page.");
    window.location.href = 'login.html';
  }
});

document.getElementById('submitPinButton').addEventListener('click', onPinSubmitted);
document.getElementById('backButton').addEventListener('click', onBackButtonClicked);

function onPinSubmitted() {
  const pinInput = document.getElementById('pin');
  const pin = pinInput.value;
  fetch(`http://localhost:8080/easyshopper_server2/viewWalletBalance?pin=${pin}`, {
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
      displayWalletBalance(data.balance)
      pinInput.disabled = true;
  })
  .catch(error => {
    console.error('Error:', error);
    alert('Error occurred while checking the PIN.');
  });
}

function displayWalletBalance(balance) {
    const walletBalanceDisplay = document.getElementById('walletBalanceDisplay');
    walletBalanceDisplay.textContent = `Wallet Balance: $${balance}`;
  }
  
  function onBackButtonClicked() {
    window.history.back(); // Go back to the previous page
  }
