document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll('.aceptar-solicitud').forEach(form => {
        form.addEventListener('submit', function (event) {
            event.preventDefault();
            let solicitudId = this.querySelector('input[name="solicitudId"]').value;
            let csrfToken = document.querySelector('input[name="_csrf"]').value;  // Obtener el token CSRF del campo oculto
            fetch(`/aceptarSolicitud`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-CSRF-TOKEN': csrfToken  // Enviar el token CSRF como encabezado personalizado
                },
                body: `solicitudId=${solicitudId}`,
            })
                .then(response => response.text())
                .then(data => {
                    if (data === 'Solicitud aceptada') {
                        document.getElementById(`solicitud-${solicitudId}`).remove();
                    } else {
                        console.error('Error al aceptar la solicitud:', data);
                    }
                })
                .catch(error => console.error('Error:', error));
        });
    });

    document.querySelectorAll('.rechazar-solicitud').forEach(form => {
        form.addEventListener('submit', function (event) {
            event.preventDefault();
            let solicitudId = this.querySelector('input[name="solicitudId"]').value;
            let csrfToken = document.querySelector('input[name="_csrf"]').value;  // Obtener el token CSRF del campo oculto
            fetch(`/rechazarSolicitud`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-CSRF-TOKEN': csrfToken
                },
                body: `solicitudId=${solicitudId}`,
            })
                .then(response => response.text())
                .then(data => {
                    if (data === 'Solicitud rechazada') {
                        document.getElementById(`solicitud-${solicitudId}`).remove();
                    } else {
                        console.error('Error al rechazar la solicitud:', data);
                    }
                })
                .catch(error => console.error('Error:', error));
        });
    });
});