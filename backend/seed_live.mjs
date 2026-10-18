import http from 'http';

function request(options, data) {
  return new Promise((resolve, reject) => {
    const req = http.request(options, (res) => {
      let body = '';
      res.on('data', chunk => body += chunk);
      res.on('end', () => {
        try {
          resolve({ status: res.statusCode, data: JSON.parse(body) });
        } catch (e) {
          resolve({ status: res.statusCode, text: body });
        }
      });
    });
    req.on('error', reject);
    if (data) req.write(JSON.stringify(data));
    req.end();
  });
}

async function run() {
  console.log('--- 1. Login Admin ---');
  const loginRes = await request({
    hostname: 'localhost',
    port: 8080,
    path: '/api/v1/auth/login',
    method: 'POST',
    headers: { 'Content-Type': 'application/json' }
  }, {
    username: 'superadmin',
    password: 'Password123!'
  });

  const token = loginRes.data?.data?.token;
  if (!token) {
    console.error('Login failed:', loginRes);
    return;
  }
  console.log('Logged in as superadmin! Token acquired.');

  const authHeaders = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  };

  console.log('--- 2. Fetch Teams ---');
  const teamsRes = await request({
    hostname: 'localhost',
    port: 8080,
    path: '/api/v1/teams',
    method: 'GET'
  });
  const teams = teamsRes.data?.data?.content || [];
  console.log('Teams count:', teams.length);
  const ind = teams.find(t => t.shortName === 'IND' || t.name === 'India') || teams[0];
  const aus = teams.find(t => t.shortName === 'AUS' || t.name === 'Australia') || teams[1];
  const eng = teams.find(t => t.shortName === 'ENG' || t.name === 'England') || teams[2];

  console.log('Teams:', { ind: ind?.id, aus: aus?.id, eng: eng?.id });

  console.log('--- 3. Add Players to England Squad ---');
  if (eng) {
    const engPlayers = [
      { fullName: 'Jos Buttler', primaryRole: 'WICKET_KEEPER', battingStyle: 'RIGHT_HAND', jerseyNumber: 63 },
      { fullName: 'Ben Stokes', primaryRole: 'ALL_ROUNDER', battingStyle: 'LEFT_HAND', jerseyNumber: 55 },
      { fullName: 'Joe Root', primaryRole: 'BATTER', battingStyle: 'RIGHT_HAND', jerseyNumber: 66 },
      { fullName: 'Liam Livingstone', primaryRole: 'ALL_ROUNDER', battingStyle: 'RIGHT_HAND', jerseyNumber: 23 },
      { fullName: 'Jofra Archer', primaryRole: 'BOWLER', battingStyle: 'RIGHT_HAND', jerseyNumber: 22 },
      { fullName: 'Harry Brook', primaryRole: 'BATTER', battingStyle: 'RIGHT_HAND', jerseyNumber: 88 },
      { fullName: 'Sam Curran', primaryRole: 'ALL_ROUNDER', battingStyle: 'LEFT_HAND', jerseyNumber: 58 },
      { fullName: 'Adil Rashid', primaryRole: 'BOWLER', battingStyle: 'RIGHT_HAND', jerseyNumber: 95 },
      { fullName: 'Phil Salt', primaryRole: 'BATTER', battingStyle: 'RIGHT_HAND', jerseyNumber: 28 },
      { fullName: 'Mark Wood', primaryRole: 'BOWLER', battingStyle: 'RIGHT_HAND', jerseyNumber: 33 }
    ];

    for (const p of engPlayers) {
      const pRes = await request({
        hostname: 'localhost',
        port: 8080,
        path: '/api/v1/players',
        method: 'POST',
        headers: authHeaders
      }, {
        fullName: p.fullName,
        nationality: 'England',
        primaryRole: p.primaryRole,
        battingStyle: p.battingStyle
      });
      const playerId = pRes.data?.data?.id;
      if (playerId) {
        await request({
          hostname: 'localhost',
          port: 8080,
          path: `/api/v1/teams/${eng.id}/players`,
          method: 'POST',
          headers: authHeaders
        }, {
          playerId: playerId,
          jerseyNumber: p.jerseyNumber
        });
      }
    }
    console.log('Created and assigned players to England!');
  }

  // Ensure India and Australia players are assigned to squad
  console.log('--- 4. Ensure India and Aus squad membership ---');
  for (let pid = 1; pid <= 3; pid++) {
    await request({
      hostname: 'localhost',
      port: 8080,
      path: `/api/v1/teams/${ind.id}/players`,
      method: 'POST',
      headers: authHeaders
    }, { playerId: pid, jerseyNumber: pid * 10 });
  }
  for (let pid = 4; pid <= 6; pid++) {
    await request({
      hostname: 'localhost',
      port: 8080,
      path: `/api/v1/teams/${aus.id}/players`,
      method: 'POST',
      headers: authHeaders
    }, { playerId: pid, jerseyNumber: pid * 7 });
  }

  console.log('--- 5. Setup Match #3 Toss & Start ---');
  const tossRes = await request({
    hostname: 'localhost',
    port: 8080,
    path: '/api/v1/matches/3/toss',
    method: 'POST',
    headers: authHeaders
  }, {
    tossWinnerTeamId: ind.id,
    tossDecision: 'BAT'
  });
  console.log('Toss response:', tossRes.status);

  const startRes = await request({
    hostname: 'localhost',
    port: 8080,
    path: '/api/v1/matches/3/start',
    method: 'POST',
    headers: authHeaders
  }, {});
  console.log('Start match response:', startRes.status);

  console.log('--- 6. Start Innings 1 ---');
  const inningsRes = await request({
    hostname: 'localhost',
    port: 8080,
    path: '/api/v1/matches/3/innings',
    method: 'POST',
    headers: authHeaders
  }, {
    inningsNumber: 1,
    openingStrikerId: 2, // Virat Kohli
    openingNonStrikerId: 1, // Rohit Sharma
    openingBowlerId: 6 // Mitchell Starc
  });

  console.log('Innings creation response:', inningsRes.status, inningsRes.data?.data?.id);
  const inningsId = inningsRes.data?.data?.id || 1;

  console.log('--- 7. Record 12 Live Deliveries ---');
  const deliveries = [
    // Over 1 (Starc bowling to Virat)
    { batterRuns: 4, extraRuns: 0, extraType: 'NONE', commentary: 'FOUR! Glorious cover drive on the up by Virat Kohli, races away to the boundary fence!' },
    { batterRuns: 1, extraRuns: 0, extraType: 'NONE', commentary: 'Single pushed into the gap at mid-on, Virat rotates the strike.' },
    { batterRuns: 0, extraRuns: 0, extraType: 'NONE', commentary: 'Dot ball. Good length delivery outside off, Rohit defends solidly into the covers.' },
    { batterRuns: 4, extraRuns: 0, extraType: 'NONE', commentary: 'FOUR! Rohit Sharma pulls in trademark swagger through square leg for four!' },
    { batterRuns: 1, extraRuns: 0, extraType: 'NONE', commentary: 'Quick single to backward point, direct hit misses.' },
    { batterRuns: 2, extraRuns: 0, extraType: 'NONE', commentary: 'Two runs. Driven wide of long-off, Kohli calls for two and makes it easily.' },
    // Over 2 (Cummins bowling to Rohit)
    { batterRuns: 0, extraRuns: 0, extraType: 'NONE', nextBowlerId: 4, commentary: 'Beaten! Pat Cummins gets extra bounce from good length, Rohit plays inside the line.' },
    { batterRuns: 0, extraRuns: 0, extraType: 'NONE', wicketType: 'CAUGHT', dismissedPlayerId: 1, fielderId: 6, newBatterId: 3, commentary: 'OUT! Caught at mid-on! Rohit mistimes the pull shot and Mitchell Starc takes a safe catch!' },
    { batterRuns: 0, extraRuns: 0, extraType: 'NONE', commentary: 'Dot ball. Jasprit Bumrah defends solidly forward.' },
    { batterRuns: 4, extraRuns: 0, extraType: 'NONE', commentary: 'FOUR! Edged and over slips! Fortunate boundary down to third man!' },
    { batterRuns: 1, extraRuns: 0, extraType: 'NONE', commentary: 'Single dabbed towards mid-wicket to give strike to Kohli.' },
    { batterRuns: 6, extraRuns: 0, extraType: 'NONE', commentary: 'SIX! Magnificent straight loft from Virat Kohli over long-on! What a colossal strike into the stands!' }
  ];

  for (let i = 0; i < deliveries.length; i++) {
    const d = deliveries[i];
    const delRes = await request({
      hostname: 'localhost',
      port: 8080,
      path: `/api/v1/innings/${inningsId}/deliveries`,
      method: 'POST',
      headers: authHeaders
    }, d);
    console.log(`Ball ${i + 1}: ${delRes.status} -> ${d.commentary.substring(0, 35)}...`);
  }

  console.log('--- SEEDING COMPLETED SUCCESSFULLY! ---');
}

run().catch(console.error);
