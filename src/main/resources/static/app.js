const api = {
  all: '/api/exams',
  search: (q) => `/api/search?courseCode=${encodeURIComponent(q)}`,
};

const resultsEl = document.getElementById('results');
const statusEl = document.getElementById('status');
const inputEl = document.getElementById('courseInput');

function googleUrl(exam) {
  const title = encodeURIComponent(`${exam.course} Exam`);
  const details = encodeURIComponent(`Room: ${exam.rooms || ''}`);
  return `https://calendar.google.com/calendar/render?action=TEMPLATE&text=${title}&dates=${exam.calendarStartTime}/${exam.calendarEndTime}&details=${details}`;
}
function outlookUrl(exam) {
  const title = encodeURIComponent(`${exam.course} Exam`);
  const body = encodeURIComponent(`Room: ${exam.rooms || ''}`);
  const s = exam.calendarStartTime, e = exam.calendarEndTime;
  const iso = (t) => `2025-${t.slice(4,6)}-${t.slice(6,8)}T${t.slice(9,11)}:${t.slice(11,13)}:00`;
  return `https://outlook.live.com/calendar/0/deeplink/compose?subject=${title}&startdt=${iso(s)}&enddt=${iso(e)}&body=${body}`;
}
function appleUrl(exam) {
  const title = encodeURIComponent(`${exam.course} Exam`);
  const desc = encodeURIComponent(`Room: ${exam.rooms || ''}`);
  return `data:text/calendar;charset=utf8,BEGIN:VCALENDAR%0AVERSION:2.0%0ABEGIN:VEVENT%0ADTSTART:${exam.calendarStartTime}%0ADTEND:${exam.calendarEndTime}%0ASUMMARY:${title}%0ADESCRIPTION:${desc}%0AEND:VEVENT%0AEND:VCALENDAR`;
}

function render(exams) {
  if (!Array.isArray(exams)) { resultsEl.innerHTML = '<p>Error loading data.</p>'; return; }
  if (!exams.length) { resultsEl.innerHTML = '<p>No results.</p>'; return; }
  resultsEl.innerHTML = exams.map(exam => `
    <div class="card">
      <div class="info">
        <div class="course">${exam.course}</div>
        <div>Section: ${exam.section || ''}</div>
        <div>Date: ${exam.date || ''}</div>
        <div>Time: ${exam.time || ''}</div>
        <div>Room: ${exam.rooms || ''}</div>
      </div>
      <div class="cal">
        <a class="btn" target="_blank" href="${googleUrl(exam)}">Google</a>
        <a class="btn" target="_blank" href="${outlookUrl(exam)}">Outlook</a>
        <a class="btn" download="exam.ics" href="${appleUrl(exam)}">Apple</a>
      </div>
    </div>
  `).join('');
}

async function loadAll() {
  statusEl.textContent = 'Loading all...';
  try { const r = await fetch(api.all); render(await r.json()); }
  catch { resultsEl.innerHTML = '<p>Failed to load exams.</p>'; }
  finally { statusEl.textContent = ''; }
}
async function search(q) {
  statusEl.textContent = `Searching ${q}...`;
  try { const r = await fetch(api.search(q)); render(await r.json()); }
  catch { resultsEl.innerHTML = '<p>Search failed.</p>'; }
  finally { statusEl.textContent = ''; }
}

document.getElementById('viewAllBtn').onclick = loadAll;
document.getElementById('searchForm').onsubmit = (e) => {
  e.preventDefault();
  const q = inputEl.value.trim();
  if (q) search(q);
};

// initial load
loadAll();
