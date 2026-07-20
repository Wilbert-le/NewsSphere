/* =============================================
   NewsSphere — Dashboard interactions
   ============================================= */

let currentTab = 'forYou';
let currentCategory = 'all';
let savedIds = new Set();

const TAB_LABELS = { forYou: 'For You', trending: 'Trending', latest: 'Latest', saved: 'Saved' };

document.addEventListener('DOMContentLoaded', () => {
    initDarkMode();
    initSidebarDefaultState();
    initAvatarDropdown();
    initSearch();
    initCategoryChips();
    restoreSavedFromStorage();
    updateFeedHeading();
});

/* ---------------- Category chips (click bằng data-category, không dùng inline onclick động) ---------------- */
function initCategoryChips() {
    document.querySelectorAll('.category-chips .chip').forEach(chip => {
        chip.addEventListener('click', () => setCategory(chip.dataset.category, chip));
    });
}

/* ---------------- Dark mode ---------------- */
function initDarkMode() {
    const saved = localStorage.getItem('ns-dark-mode');
    const isDark = saved === '1';
    document.body.classList.toggle('dark-mode', isDark);
    updateDarkModeIcon(isDark);
}
function toggleDarkMode() {
    const isDark = document.body.classList.toggle('dark-mode');
    localStorage.setItem('ns-dark-mode', isDark ? '1' : '0');
    updateDarkModeIcon(isDark);
}
function updateDarkModeIcon(isDark) {
    const icon = document.getElementById('darkModeIcon');
    if (icon) icon.className = isDark ? 'ti ti-sun' : 'ti ti-moon';
}

/* ---------------- Sidebar (drawer) ---------------- */
function initSidebarDefaultState() {
    // Trên màn hình rộng, mở sẵn sidebar để không phải bấm hamburger mỗi lần
    if (window.innerWidth >= 1100) {
        document.body.classList.add('sidebar-default-open');
    }
}
function toggleSidebar() {
    if (document.body.classList.contains('sidebar-default-open')) {
        document.body.classList.remove('sidebar-default-open');
        document.getElementById('sidebarLeft').classList.remove('open');
        document.getElementById('sbOverlay').classList.remove('open');
        return;
    }
    const sidebar = document.getElementById('sidebarLeft');
    const overlay = document.getElementById('sbOverlay');
    const willOpen = !sidebar.classList.contains('open');
    sidebar.classList.toggle('open', willOpen);
    overlay.classList.toggle('open', willOpen && window.innerWidth < 1100);
    if (willOpen && window.innerWidth >= 1100) {
        document.body.classList.add('sidebar-default-open');
    }
}
function closeSidebar() {
    document.getElementById('sidebarLeft').classList.remove('open');
    document.getElementById('sbOverlay').classList.remove('open');
    document.body.classList.remove('sidebar-default-open');
}

/* ---------------- Avatar dropdown ---------------- */
function initAvatarDropdown() {
    const btn = document.getElementById('avatarBtn');
    const dropdown = document.getElementById('avatarDropdown');
    if (!btn || !dropdown) return;
    btn.addEventListener('click', (e) => {
        e.stopPropagation();
        dropdown.classList.toggle('open');
    });
    document.addEventListener('click', () => dropdown.classList.remove('open'));
}

/* ---------------- Feed tabs ---------------- */
function switchTab(tab) {
    currentTab = tab;
    document.querySelectorAll('.feed-section').forEach(s => s.style.display = 'none');
    document.getElementById('feed-' + tab).style.display = '';
    document.querySelectorAll('.feed-nav-item[data-tab]').forEach(el => {
        el.classList.toggle('active', el.dataset.tab === tab);
    });
    document.querySelectorAll('.feed-tab[data-tab]').forEach(el => {
        el.classList.toggle('active', el.dataset.tab === tab);
    });
    updateFeedHeading();
    applyCategoryFilter();
    if (window.innerWidth < 1100) closeSidebar();
}

/* ---------------- Feed heading (title + subtitle) ---------------- */
function updateFeedHeading() {
    const title = document.getElementById('feedTitle');
    if (title) {
        const base = TAB_LABELS[currentTab] || 'Feed';
        title.textContent = currentCategory === 'all' ? base : base + ' · ' + currentCategory;
    }
    updateFeedSub();
}
function updateFeedSub() {
    const sub = document.getElementById('feedSub');
    if (!sub) return;
    const section = document.getElementById('feed-' + currentTab);
    if (!section) return;
    const visible = Array.from(section.querySelectorAll('.news-card, .hero-card'))
        .filter(card => card.style.display !== 'none').length;
    sub.textContent = visible + (visible === 1 ? ' story' : ' stories') + ' · Updated just now';
}

/* ---------------- Category filter ---------------- */
function setCategory(cat, el) {
    currentCategory = cat;
    document.querySelectorAll('.chip').forEach(c => c.classList.remove('active'));
    el.classList.add('active');
    updateFeedHeading();
    applyCategoryFilter();
    if (window.innerWidth < 1100) closeSidebar();
}
function applyCategoryFilter() {
    const section = document.getElementById('feed-' + currentTab);
    if (!section) return;
    section.querySelectorAll('.news-card, .hero-card').forEach(card => {
        const match = currentCategory === 'all' || card.dataset.category === currentCategory;
        card.style.display = match ? '' : 'none';
    });
    updateFeedSub();
}

/* ---------------- Search ---------------- */
function initSearch() {
    const input = document.getElementById('searchInput');
    if (!input) return;
    input.addEventListener('input', () => {
        const q = input.value.trim().toLowerCase();
        document.querySelectorAll('.news-card, .hero-card').forEach(card => {
            const title = card.querySelector('.news-title')?.textContent.toLowerCase() || '';
            const source = card.querySelector('.source')?.textContent.toLowerCase() || '';
            const cat = (card.dataset.category || '').toLowerCase();
            const match = !q || title.includes(q) || source.includes(q) || cat.includes(q);
            card.style.display = match ? '' : 'none';
        });
        updateFeedSub();
    });
}

/* ---------------- Market tabs ---------------- */
function setMarketTab(cls, el) {
    el.parentElement.querySelectorAll('.market-tab').forEach(t => t.classList.remove('active'));
    el.classList.add('active');
    document.querySelectorAll('.market-group').forEach(g => g.classList.remove('active'));
    document.getElementById('market-' + cls).classList.add('active');
}

/* ---------------- Bookmarks / Saved ---------------- */
function restoreSavedFromStorage() {
    try {
        const stored = JSON.parse(localStorage.getItem('ns-saved-ids') || '[]');
        savedIds = new Set(stored);
    } catch (e) {
        savedIds = new Set();
    }
    savedIds.forEach(id => {
        document.querySelectorAll('.bookmark-btn[data-article-id="' + id + '"]').forEach(btn => btn.classList.add('saved'));
    });
    renderSavedTab();
}
function toggleBookmark(btn) {
    const id = btn.dataset.articleId;
    const nowSaved = !btn.classList.contains('saved');
    document.querySelectorAll('.bookmark-btn[data-article-id="' + id + '"]').forEach(b => b.classList.toggle('saved', nowSaved));
    if (nowSaved) savedIds.add(id); else savedIds.delete(id);
    localStorage.setItem('ns-saved-ids', JSON.stringify(Array.from(savedIds)));
    renderSavedTab();
}
function renderSavedTab() {
    const grid = document.getElementById('savedGrid');
    const emptyState = document.getElementById('savedEmptyState');
    const navCount = document.getElementById('savedNavCount');
    if (navCount) navCount.textContent = savedIds.size;
    const savedTab = document.getElementById('savedTab');
    if (savedTab) savedTab.textContent = 'Saved (' + savedIds.size + ')';
    if (!grid) return;

    grid.querySelectorAll('[data-cloned="1"]').forEach(el => el.remove());

    if (savedIds.size === 0) {
        if (emptyState) emptyState.style.display = '';
        return;
    }
    if (emptyState) emptyState.style.display = 'none';

    savedIds.forEach(id => {
        const source = document.querySelector('#feed-forYou .news-card[data-article-id="' + id + '"], #feed-forYou .hero-card[data-article-id="' + id + '"]')
            || document.querySelector('.news-card[data-article-id="' + id + '"], .hero-card[data-article-id="' + id + '"]');
        if (!source) return;
        const clone = source.cloneNode(true);
        clone.dataset.cloned = '1';
        clone.style.display = '';
        clone.classList.remove('hero-card');
        clone.classList.add('news-card');
        grid.appendChild(clone);
    });
}

/* ---------------- Settings panel ---------------- */
function openSettings() {
    document.getElementById('settingsPanel').classList.add('open');
    document.getElementById('settingsOverlay').classList.add('open');
    renderSettingsToggles();
}
function closeSettings() {
    document.getElementById('settingsPanel').classList.remove('open');
    document.getElementById('settingsOverlay').classList.remove('open');
}
function renderSettingsToggles() {
    const catBox = document.getElementById('catToggles');
    const mktBox = document.getElementById('mktToggles');
    if (catBox && window.NS_CATEGORIES) {
        catBox.innerHTML = window.NS_CATEGORIES.map(c => `
            <div class="toggle-item on" onclick="this.classList.toggle('on')">
                <span class="toggle-label">${c}</span>
                <div class="toggle-check"></div>
            </div>`).join('');
    }
    if (mktBox && window.NS_MARKET_ASSETS) {
        mktBox.innerHTML = window.NS_MARKET_ASSETS.map(m => `
            <div class="toggle-item on" onclick="this.classList.toggle('on')">
                <span class="toggle-label">${m}</span>
                <div class="toggle-check"></div>
            </div>`).join('');
    }
}
function saveSettings() {
    closeSettings();
    showToast('Preferences saved');
}

/* ---------------- Daily briefing (placeholder) ---------------- */
function generateBriefing() {
    showToast('Briefing generation is coming soon');
}

/* ---------------- Toast ---------------- */
function showToast(msg) {
    let toast = document.querySelector('.toast');
    if (!toast) {
        toast = document.createElement('div');
        toast.className = 'toast';
        document.body.appendChild(toast);
    }
    toast.textContent = msg;
    toast.classList.add('show');
    clearTimeout(showToast._t);
    showToast._t = setTimeout(() => toast.classList.remove('show'), 2200);
}