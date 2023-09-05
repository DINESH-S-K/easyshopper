import AuthUtility from "../authutility.js";

const auditTableBody = document.getElementById('auditTableBody');

AuthUtility.userInfo().then(data => {
    console.log(data);
    if (data && data.roleId === 1) {
        fetch('http://localhost:8080/easyshopper_server2/auditLogs', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include'
        })

            .then(response => response.json())
            .then(data => {
                data.forEach(log => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
    <td>${log.id}</td>
    <td>${log.userId}</td>
    <td>${log.username}</td>
    <td>${log.dateAndTime}</td>
    <td>${log.role}</td>
    <td>${log.action}</td>
  `;
                    auditTableBody.appendChild(row);
                });
            })
            .catch(error => {
                console.error('Error fetching audit logs:', error);
            });
    } else {
        alert("Access denied. You are not authorized to access this page.");
        window.location.href = 'login.html';
    }
});

