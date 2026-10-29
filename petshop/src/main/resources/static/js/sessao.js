/* Controle de inatividade no navegador (30 s).
 * O SERVIDOR também valida a expiração em cada requisição (SessaoInterceptor). */
(function () {
  var LIMITE = 30000;   // 30 s
  var AVISO = 25000;    // aviso visual aos 25 s (bônus)
  var ultimo = Date.now();
  var ultimoPing = 0;
  var bloqueado = false;
  var aviso = document.getElementById('aviso-sessao');
  var contador = document.getElementById('aviso-seg');
  var bloqueio = document.getElementById('bloqueio');

  function atividade() {
    if (bloqueado) return;
    var agora = Date.now();
    ultimo = agora;                              // reinicia a contagem (um único relógio, sem timers múltiplos)
    if (aviso) aviso.classList.add('d-none');
    if (agora - ultimoPing > 2000) {             // avisa o servidor (renova a sessão lá também)
      ultimoPing = agora;
      fetch('/ping', { credentials: 'same-origin', cache: 'no-store' }).catch(function () {});
    }
  }

  ['click', 'keydown', 'mousemove', 'scroll', 'wheel', 'touchstart', 'input'].forEach(function (ev) {
    window.addEventListener(ev, atividade, { passive: true, capture: true });
  });

  function expirar() {
    bloqueado = true;
    if (bloqueio) bloqueio.style.display = 'flex';   // bloqueia a tela
    setTimeout(function () { window.location.href = '/logout?motivo=inatividade'; }, 1500);
  }

  // Compara com Date.now(): continua correto mesmo se a aba ficou em segundo plano
  function checar() {
    if (bloqueado) return;
    var ocioso = Date.now() - ultimo;
    if (ocioso >= LIMITE) { expirar(); return; }
    if (aviso && ocioso >= AVISO) {
      aviso.classList.remove('d-none');
      if (contador) contador.textContent = Math.max(0, Math.ceil((LIMITE - ocioso) / 1000));
    }
  }
  setInterval(checar, 500);
  document.addEventListener('visibilitychange', checar);
})();
