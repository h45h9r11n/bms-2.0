document.addEventListener('DOMContentLoaded', function () {
    const loginForm = document.querySelector('section');
    loginForm.style.opacity = 0;

    setTimeout(() => {
        loginForm.style.transition = 'opacity 1s ease-in-out';
        loginForm.style.opacity = 1;
    }, 500);

    const loginButton = document.querySelector('button');
    loginButton.addEventListener('click', function () {
        const usernameInput = document.querySelector('input[type="username"]');
        const passwordInput = document.querySelector('input[type="password"]');

        const isValid = usernameInput.checkValidity() && passwordInput.checkValidity();

        if (!isValid) {
            loginForm.classList.add('shake');

            setTimeout(() => {
                loginForm.classList.remove('shake');
            }, 1000);
        }
    });
});
