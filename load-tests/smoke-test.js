import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 1,
  duration: '20s',
  thresholds: {
    http_req_duration: ['p(95)<2000'],
    http_req_failed: ['rate<0.1'],
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function() {
  console.log(`Smoke test started against ${BASE_URL}`);

  // GET запросы
  const getEndpoints = ['/api/overview', '/api/constellations', '/api/satellites'];

  for (const endpoint of getEndpoints) {
    const response = http.get(`${BASE_URL}${endpoint}`);
    check(response, { [`GET ${endpoint}`]: (r) => r.status === 200 });
    sleep(1);
  }

  // Полный цикл: создание, добавление спутника, миссия, удаление
  const testName = `Smoke-${Date.now()}`;

  // POST — создание группировки
  const createRes = http.post(
    `${BASE_URL}/api/constellations`,
    JSON.stringify({ name: testName }),
    { headers: { 'Content-Type': 'application/json' } }
  );
  check(createRes, { 'POST create constellation': (r) => r.status === 201 });

  if (createRes.status === 201) {
    const id = createRes.json('id');

    // POST — добавление спутников
    const addRes = http.post(
      `${BASE_URL}/api/add-satellites`,
      JSON.stringify({
        constellationName: testName,
        satelliteParams: [
          { type: 'COMMUNICATION', name: 'SmokeSat-1', batteryLevel: 0.9, bandwidth: 100.0 },
        ],
      }),
      { headers: { 'Content-Type': 'application/json' } }
    );
    check(addRes, { 'POST add satellites': (r) => r.status === 200 });

    sleep(1);

    // POST — выполнение миссии
    const missionRes = http.post(
      `${BASE_URL}/api/missions`,
      JSON.stringify({ targetType: 'CONSTELLATION', constellationName: testName }),
      { headers: { 'Content-Type': 'application/json' } }
    );
    check(missionRes, { 'POST execute mission': (r) => r.status === 200 });

    sleep(1);

    // DELETE — удаление группировки
    const deleteRes = http.del(`${BASE_URL}/api/constellations/${id}`);
    check(deleteRes, { 'DELETE constellation': (r) => r.status === 204 });
  }

  console.log('Smoke test completed');
}