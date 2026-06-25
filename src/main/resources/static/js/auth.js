// =============================================
// NewsSphere — Auth Page Scripts
// =============================================

document.addEventListener('DOMContentLoaded', function () {

    // ---- Interest Chips ----
    const interests = [
        { icon: 'ti-cpu',                  label: 'Technology'    },
        { icon: 'ti-chart-bar',            label: 'Finance'       },
        { icon: 'ti-bolt',                 label: 'AI & Science'  },
        { icon: 'ti-briefcase',            label: 'Business'      },
        { icon: 'ti-shield',               label: 'Cybersecurity' },
        { icon: 'ti-world',                label: 'World News'    },
        { icon: 'ti-activity',             label: 'Sports'        },
        { icon: 'ti-heart',                label: 'Health'        },
    ];

    let selected = new Set([0, 1, 2]); // mặc định chọn 3 cái đầu

    function renderInterests() {
        const grid = document.getElementById('interestGrid');
        if (!grid) return;
        grid.innerHTML = interests.map((it, i) => `
            <div class="interest-chip${selected.has(i) ? ' on' : ''}"
                 onclick="toggleInterest(${i})"
                 data-index="${i}">
                <i class="ti ${it.icon}" aria-hidden="true"></i>
                <span>${it.label}</span>
            </div>
        `).join('');
        updateHiddenInputs();
    }

    window.toggleInterest = function (i) {
        if (selected.has(i)) {
            selected.delete(i);
        } else {
            selected.add(i);
        }
        renderInterests();
    };

    function updateHiddenInputs() {
        // Xóa hidden inputs cũ
        document.querySelectorAll('.interests-hidden').forEach(el => el.remove());
        const form = document.getElementById('registerForm');
        if (!form) return;
        // Tạo hidden input cho mỗi interest được chọn
        selected.forEach(i => {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'interests';
            input.value = interests[i].label;
            input.className = 'interests-hidden';
            form.appendChild(input);
        });
    }

    // ---- Tab Switching ----
    window.switchTab = function (tab) {
        const isLogin = tab === 'login';

        document.getElementById('tabLogin').classList.toggle('active', isLogin);
        document.getElementById('tabRegister').classList.toggle('active', !isLogin);

        const loginForm = document.getElementById('loginForm');
        const registerForm = document.getElementById('registerForm');

        if (isLogin) {
            loginForm.style.display = 'flex';
            registerForm.style.display = 'none';
            document.getElementById('formTitle').textContent = 'Welcome back';
            document.getElementById('formSubtitle').textContent = 'Sign in to your NewsSphere account';
        } else {
            loginForm.style.display = 'none';
            registerForm.style.display = 'flex';
            document.getElementById('formTitle').textContent = 'Create your account';
            document.getElementById('formSubtitle').textContent = "Join NewsSphere — it's free to start";
        }
    };

    // ---- Password Toggle ----
    window.togglePwd = function (id, btn) {
        const inp = document.getElementById(id);
        const show = inp.type === 'password';
        inp.type = show ? 'text' : 'password';
        btn.querySelector('i').className = show ? 'ti ti-eye-off' : 'ti ti-eye';
    };

    // ---- Auto-switch tab nếu server trả về activeTab = register ----
    const activeTabMeta = document.getElementById('activeTabData');
    if (activeTabMeta && activeTabMeta.dataset.tab === 'register') {
        switchTab('register');
    }

    // ---- Init ----
    renderInterests();
});
