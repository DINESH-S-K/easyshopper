const AuthUtility = {
  userInfo: function () {
    return fetch('http://localhost:8080/easyshopper_server2/getUserInfo', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    })
    .then(response => {
      if (!response.ok && response.status !=204) {
        throw new Error('UNAUTHORIZED');
      }
      return response.json();
    })
    .catch(error => {
      console.error('Error:', error);
    });
  }
}

export default AuthUtility;