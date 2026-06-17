// Генератор тестовых данных для нагрузочного тестирования

export function generateConstellationData() {
  const timestamp = Date.now();
  const random = Math.random().toString(36).substr(2, 8);
  return {
    name: `LoadTest-Const-${timestamp}-${random}`,
  };
}

export function generateSatelliteData(constellationName) {
  return {
    constellationName: constellationName,
    satelliteParams: [
      {
        type: 'COMMUNICATION',
        name: `CommSat-${Date.now()}`,
        batteryLevel: 0.9,
        bandwidth: 100.0,
      },
      {
        type: 'IMAGE',
        name: `ImgSat-${Date.now()}`,
        batteryLevel: 0.85,
        resolution: 0.5,
      },
    ],
  };
}

export function generateMissionData(constellationName, targetType = 'CONSTELLATION') {
  return {
    targetType: targetType,
    constellationName: constellationName,
  };
}

export function generateBatchConstellations(count = 10) {
  const constellations = [];
  for (let i = 0; i < count; i++) {
    constellations.push(generateConstellationData());
  }
  return constellations;
}