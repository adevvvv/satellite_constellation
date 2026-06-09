import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";

const missionExecutions = new Counter('mission_executions');
const constellationCreations = new Counter('constellation_creations');
const satelliteAdditions = new Counter('satellite_additions');
const requestDuration = new Trend('request_duration');
const successRate = new Rate('success_rate');

export const options = {
  stages: [
    { duration: '10s', target: 10 },
    { duration: '20s', target: 50 },
    { duration: '30s', target: 50 },
    { duration: '20s', target: 100 },
    { duration: '30s', target: 100 },
    { duration: '10s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'],
    http_req_failed: ['rate<0.1'],
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

function generateUniqueName(prefix) {
  return `${prefix}-${Date.now()}-${Math.random().toString(36).substr(2, 5)}`;
}

export default function() {
  const vuId = __VU;
  const iterId = __ITER;
  const constellationName = generateUniqueName('LoadTest');
  let constellationId = null;

  // 1. GET — Системная сводка
  group('1. Получение системной сводки', () => {
    const response = http.get(`${BASE_URL}/api/overview`, {
      tags: { name: 'GetOverview' },
    });
    check(response, {
      'overview retrieved': (r) => r.status === 200,
    });
    requestDuration.add(response.timings.duration);
    successRate.add(response.status === 200);
    sleep(1);
  });

  // 2. GET — Список группировок
  group('2. Получение списка группировок', () => {
    const response = http.get(`${BASE_URL}/api/constellations`, {
      tags: { name: 'GetConstellations' },
    });
    check(response, {
      'constellations retrieved': (r) => r.status === 200,
    });
    requestDuration.add(response.timings.duration);
    successRate.add(response.status === 200);
    sleep(1);
  });

  // 3. GET — Список спутников
  group('3. Получение списка спутников', () => {
    const response = http.get(`${BASE_URL}/api/satellites`, {
      tags: { name: 'GetSatellites' },
    });
    check(response, {
      'satellites retrieved': (r) => r.status === 200,
    });
    requestDuration.add(response.timings.duration);
    successRate.add(response.status === 200);
    sleep(1);
  });

  // 4. POST — Создание группировки
  group('4. Создание группировки спутников', () => {
    const payload = JSON.stringify({ name: constellationName });
    const response = http.post(`${BASE_URL}/api/constellations`, payload, {
      headers: { 'Content-Type': 'application/json' },
      tags: { name: 'CreateConstellation' },
    });
    const ok = check(response, {
      'constellation created': (r) => r.status === 201,
    });
    if (response.status === 201) {
      try {
        constellationId = response.json('id');
        constellationCreations.add(1);
      } catch (e) {
        console.error(`Cannot parse ID for ${constellationName}`);
      }
    }
    requestDuration.add(response.timings.duration);
    successRate.add(ok);
    sleep(2);
  });

  // 5. POST — Добавление спутников
  group('5. Добавление спутников в группировку', () => {
    if (!constellationId) return;
    const payload = JSON.stringify({
      constellationName: constellationName,
      satelliteParams: [
        { type: 'COMMUNICATION', name: `Comm-${vuId}-${iterId}`, batteryLevel: 0.9, bandwidth: 100.0 },
        { type: 'IMAGE', name: `Img-${vuId}-${iterId}`, batteryLevel: 0.85, resolution: 0.5 },
      ],
    });
    const response = http.post(`${BASE_URL}/api/add-satellites`, payload, {
      headers: { 'Content-Type': 'application/json' },
      tags: { name: 'AddSatellites' },
    });
    const ok = check(response, {
      'satellites added': (r) => r.status === 200,
    });
    if (response.status === 200) satelliteAdditions.add(1);
    requestDuration.add(response.timings.duration);
    successRate.add(ok);
    sleep(2);
  });

  // 6. POST — Выполнение миссии
  group('6. Выполнение миссии', () => {
    const payload = JSON.stringify({
      targetType: 'CONSTELLATION',
      constellationName: constellationName,
    });
    const response = http.post(`${BASE_URL}/api/missions`, payload, {
      headers: { 'Content-Type': 'application/json' },
      tags: { name: 'ExecuteMission' },
    });
    const ok = check(response, {
      'mission executed': (r) => r.status === 200,
    });
    if (response.status === 200) missionExecutions.add(1);
    requestDuration.add(response.timings.duration);
    successRate.add(ok);
    sleep(3);
  });

  // 7. GET — Статистика группировки
  group('7. Получение статистики группировки', () => {
    const response = http.get(
      `${BASE_URL}/api/constellations/${constellationName}/statistics`,
      { tags: { name: 'GetStatistics' } }
    );
    const ok = check(response, {
      'statistics retrieved': (r) => r.status === 200,
    });
    requestDuration.add(response.timings.duration);
    successRate.add(ok);
    sleep(1);
  });

  // 8. POST — Активация спутников
  group('8. Активация спутников', () => {
    const response = http.post(
      `${BASE_URL}/api/constellations/${constellationName}/activate`,
      null,
      { tags: { name: 'ActivateSatellites' } }
    );
    const ok = check(response, {
      'activation successful': (r) => r.status === 200,
    });
    requestDuration.add(response.timings.duration);
    successRate.add(ok);
    sleep(1);
  });

  // 9. POST — Деактивация спутников
  group('9. Деактивация спутников', () => {
    const response = http.post(
      `${BASE_URL}/api/constellations/${constellationName}/deactivate`,
      null,
      { tags: { name: 'DeactivateSatellites' } }
    );
    const ok = check(response, {
      'deactivation successful': (r) => r.status === 200,
    });
    requestDuration.add(response.timings.duration);
    successRate.add(ok);
    sleep(1);
  });

  // 10. DELETE — Удаление группировки
  group('10. Удаление группировки', () => {
    if (constellationId) {
      const response = http.del(`${BASE_URL}/api/constellations/${constellationId}`, null, {
        tags: { name: 'DeleteConstellation' },
      });
      const ok = check(response, {
        'constellation deleted': (r) => r.status === 204,
      });
      requestDuration.add(response.timings.duration);
      successRate.add(ok);
    }
    sleep(1);
  });
}

export function handleSummary(data) {
  return {
    'load-tests/results/summary.html': htmlReport(data, {
      title: 'Satellite Constellation — Load Test Report',
    }),
    'load-tests/results/summary.json': JSON.stringify(data, null, 2),
    stdout: generateTextSummary(data),
  };
}

function generateTextSummary(data) {
  const m = data.metrics || {};
  return `
============================================================
  РЕЗУЛЬТАТЫ НАГРУЗОЧНОГО ТЕСТИРОВАНИЯ
============================================================

Длительность: ${((data.state?.testRunDurationMs || 0) / 1000).toFixed(2)}с
Максимум пользователей: ${m.vus_max?.values?.max || 0}
Всего итераций: ${m.iterations?.values?.count || 0}
Всего запросов: ${m.http_reqs?.values?.count || 0}
Успешность HTTP: ${((1 - (m.http_req_failed?.values?.rate || 0)) * 100).toFixed(2)}%

Времена ответа (ms):
  Среднее: ${(m.http_req_duration?.values?.avg || 0).toFixed(2)}
  Медиана: ${(m.http_req_duration?.values?.med || 0).toFixed(2)}
  p90: ${(m.http_req_duration?.values?.['p(90)'] || 0).toFixed(2)}
  p95: ${(m.http_req_duration?.values?.['p(95)'] || 0).toFixed(2)}

Бизнес-операции:
  Создано группировок: ${constellationCreations.value || 0}
  Добавлено спутников: ${satelliteAdditions.value || 0}
  Выполнено миссий: ${missionExecutions.value || 0}

Проверки:
  Всего: ${m.checks?.values?.passes + m.checks?.values?.fails || 0}
  Успешно: ${m.checks?.values?.passes || 0}
  Ошибок: ${m.checks?.values?.fails || 0}
============================================================
`;
}