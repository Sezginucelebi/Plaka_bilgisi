window.LoginController = {
    handleLogin: async function() {
        const emailInput = document.getElementById('login-email');
        const passwordInput = document.getElementById('login-pass');
        const errorEl = document.getElementById('login-error');
        const remember = document.getElementById('remember-me').checked;

        if(!emailInput.value || !passwordInput.value) {
            errorEl.innerText = "Lutfen tum alanlari doldurun.";
            return;
        }

        errorEl.innerText = "Kimlik dogrulaniyor...";

        try {
            const res = await Engine.ipc.invoke('login-user', {
                email: emailInput.value.trim(),
                password: passwordInput.value.trim()
            });

            if(res.success) {
                if(remember) {
                    localStorage.setItem('auronova_e', emailInput.value);
                    localStorage.setItem('auronova_p', passwordInput.value);
                } else {
                    localStorage.removeItem('auronova_e');
                    localStorage.removeItem('auronova_p');
                }

                Engine.currentUser = res.user;
                this.redirectByUserRole(res.user);
            } else {
                errorEl.innerText = "Hata: " + res.error;
                passwordInput.value = "";
            }
        } catch (e) {
            errorEl.innerText = "Sunucu baglantisi koptu.";
        }
    },

    redirectByUserRole: function(user) {
        Engine.loadShell();

        if(Engine.isAdmin(user)) {
            document.getElementById('sales-menu').style.display = 'block';
            document.getElementById('firm-context-menu').style.display = 'none';
            Engine.navigate('admin-firms');
        } else {
            document.getElementById('sales-menu').style.display = 'none';
            Engine.openFirm(user.uid);
        }
    }
};
