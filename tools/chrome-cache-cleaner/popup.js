/**
 * Cache Cleaner - Chrome 扩展
 * 用于清除页面缓存并强制刷新
 */

// 显示状态消息
function showStatus(message) {
  const status = document.getElementById('status');
  status.textContent = message;
  status.classList.add('show');
  setTimeout(() => {
    status.classList.remove('show');
  }, 2000);
}

// 在当前标签页执行脚本
async function executeScript(func) {
  const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
  if (tab?.id) {
    await chrome.scripting.executeScript({
      target: { tabId: tab.id },
      func: func
    });
  }
}

// 清除 localStorage 和 sessionStorage
async function clearStorage() {
  await executeScript(() => {
    localStorage.clear();
    sessionStorage.clear();
    // 清除 IndexedDB
    if (indexedDB.databases) {
      indexedDB.databases().then(dbs => {
        dbs.forEach(db => indexedDB.deleteDatabase(db.name));
      });
    }
  });
  showStatus('✅ Storage 已清除');
}

// 清除浏览器缓存
async function clearBrowserCache() {
  await chrome.browsingData.removeCache({ since: 0 });
  showStatus('✅ 浏览器缓存已清除');
}

// 强制刷新页面
async function hardReload() {
  await executeScript(() => {
    location.reload(true);
  });
  window.close();
}

// 清除全部并刷新
async function clearAllAndReload() {
  showStatus('🔄 正在清除...');

  // 清除 Storage
  await executeScript(() => {
    localStorage.clear();
    sessionStorage.clear();
    // 清除 Service Worker 缓存
    if ('caches' in window) {
      caches.keys().then(names => {
        names.forEach(name => caches.delete(name));
      });
    }
    // 注销 Service Worker
    if ('serviceWorker' in navigator) {
      navigator.serviceWorker.getRegistrations().then(registrations => {
        registrations.forEach(registration => registration.unregister());
      });
    }
  });

  // 清除浏览器缓存
  await chrome.browsingData.removeCache({ since: 0 });

  // 刷新页面
  await executeScript(() => {
    location.reload(true);
  });

  window.close();
}

// 绑定事件
document.getElementById('clearAll').addEventListener('click', clearAllAndReload);
document.getElementById('clearStorage').addEventListener('click', clearStorage);
document.getElementById('clearCache').addEventListener('click', clearBrowserCache);
document.getElementById('hardReload').addEventListener('click', hardReload);
