const choreGrid = document.getElementById('chore-grid');
const statusBar = document.getElementById('status-bar');

const kidLabels = {
    ONE: 'Kid One',
    TWO: 'Kid Two'
};

function friendlyKid(kidEnum, fallbackName) {
    if (fallbackName && fallbackName.trim().length > 0) {
        return fallbackName;
    }
    return kidLabels[kidEnum] ?? kidEnum;
}

function formatDate(dateString) {
    if (!dateString) return '—';
    const date = new Date(dateString);
    return new Intl.DateTimeFormat(undefined, {
        weekday: 'short',
        month: 'short',
        day: 'numeric',
        hour: 'numeric',
        minute: '2-digit'
    }).format(date);
}

function setStatus(message, tone = 'success') {
    statusBar.textContent = message;
    statusBar.classList.remove('hidden');
    statusBar.style.background = tone === 'error' ? '#fee2e2' : '#ecfccb';
    statusBar.style.color = tone === 'error' ? '#b91c1c' : '#3f6212';
    statusBar.style.borderColor = tone === 'error' ? '#fecdd3' : '#d9f99d';
    setTimeout(() => statusBar.classList.add('hidden'), 2500);
}

async function fetchChores() {
    const response = await fetch('/api/chores');
    if (!response.ok) {
        throw new Error('Unable to load chores');
    }
    const chores = await response.json();
    if (chores.length > 0) {
        kidLabels.ONE = chores[0].kidOneName || kidLabels.ONE;
        kidLabels.TWO = chores[0].kidTwoName || kidLabels.TWO;
    }
    return chores;
}

function renderChores(chores) {
    choreGrid.innerHTML = '';

    if (!chores || chores.length === 0) {
        const empty = document.createElement('div');
        empty.className = 'empty-state';
        empty.textContent = 'No chores yet. Add some in the backend seed config.';
        choreGrid.appendChild(empty);
        return;
    }

    chores.forEach((chore) => {
        const card = document.createElement('article');
        card.className = 'card';

        const name = document.createElement('h3');
        name.className = 'chore-name';
        name.textContent = chore.name;

        const meta = document.createElement('div');
        meta.className = 'meta';
        meta.innerHTML = `
            <span class="pill assigned">Assigned: ${friendlyKid(chore.assignedTo, chore.assignedToName)}</span>
            <span class="pill due">Next due: ${formatDate(chore.nextDueDate)}</span>
            <span class="pill">Frequency: ${chore.frequency === 'DAILY' ? 'Every day' : 'Every 3 days'}</span>
        `;

        const last = document.createElement('div');
        last.className = 'meta';
        last.innerHTML = `Last completed: ${chore.lastCompletedAt ? formatDate(chore.lastCompletedAt) : 'Not yet'}
            ${chore.lastCompletedBy ? 'by ' + friendlyKid(chore.lastCompletedBy, chore.lastCompletedByName) : ''}`;

        const actions = document.createElement('div');
        actions.className = 'actions';

        const completeAssigned = document.createElement('button');
        completeAssigned.textContent = `Mark done (${friendlyKid(chore.assignedTo, chore.assignedToName)})`;
        completeAssigned.onclick = () => markDone(chore.id, chore.assignedTo);

        const completeOther = document.createElement('button');
        completeOther.className = 'secondary';
        const otherKid = chore.assignedTo === 'ONE' ? 'TWO' : 'ONE';
        completeOther.textContent = `Mark done (${friendlyKid(otherKid, kidLabels[otherKid])})`;
        completeOther.onclick = () => markDone(chore.id, otherKid);

        actions.append(completeAssigned, completeOther);

        card.append(name, meta, last, actions);
        choreGrid.appendChild(card);
    });
}

async function markDone(id, completedBy) {
    try {
        const response = await fetch(`/api/chores/${id}/complete`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ completedBy })
        });
        if (!response.ok) {
            throw new Error('Could not update chore');
        }
        await loadChores();
        setStatus('Chore updated. Next turn assigned automatically.');
    } catch (err) {
        setStatus(err.message || 'Something went wrong', 'error');
    }
}

async function loadChores() {
    try {
        const chores = await fetchChores();
        renderChores(chores);
    } catch (err) {
        setStatus(err.message || 'Unable to load chores', 'error');
    }
}

loadChores();
