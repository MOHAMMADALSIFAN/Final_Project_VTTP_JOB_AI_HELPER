// This will be appended to the generated service worker
self.addEventListener('message', (event) => {
  if (event.data && event.data.action === 'clearCache') {
    console.log('Clearing service worker caches');
    self.caches.keys().then(cacheNames => {
      return Promise.all(
        cacheNames.map(cacheName => {
          console.log('Deleting cache:', cacheName);
          return self.caches.delete(cacheName);
        })
      );
    });
  }
});