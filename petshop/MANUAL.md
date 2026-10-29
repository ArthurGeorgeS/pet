# 📘 Manual do Usuário — Sistema PetShop

## 1. Primeiro acesso
1. Abra **http://localhost:8080**.
2. Na tela **Primeiro acesso**, informe um usuário (mín. 3 caracteres), senha (mín. 6) e a confirmação. Clique em **Criar administrador**.
3. Faça login com os dados criados. *(Esta tela só aparece uma vez.)*

## 2. Sessão e segurança — leia!
- Por segurança, se você ficar **30 segundos sem usar o sistema** (sem clicar, digitar, rolar ou mexer o mouse), a sessão **encerra sozinha**.
- Aos **25 segundos** aparece um aviso amarelo no canto da tela com a contagem. Basta mexer o mouse ou digitar para continuar.
- Ao expirar, a tela escurece e você volta ao login com a mensagem *"Sua sessão foi encerrada por inatividade"*.
- ⚠️ O que estava sendo digitado e não foi salvo **é perdido**. Salve com frequência.
- Use **Sair** (menu lateral) ao terminar.

## 3. Navegação
Menu lateral (no celular fica no topo): **Painel · Tutores · Pets · Agendamentos · Produtos · Sair**.

## 4. Painel
Mostra: agendamentos de hoje (lista com horário, pet, serviço e status), total de clientes, total de pets e quantidade de produtos com estoque abaixo de 5.

## 5. Tutores (clientes)
- **Cadastrar:** *+ Novo tutor* → preencha nome, telefone (com DDD), CPF (só números ou com pontuação), e-mail e endereço → **Salvar**.
- **Buscar:** digite parte do nome e clique em *Buscar*.
- **Editar:** botão *Editar* na linha. **Excluir:** botão *Excluir* → confirme.
- Mensagens de erro aparecem **embaixo do campo** com problema (ex.: CPF inválido ou já cadastrado).
- Na lista, o CPF aparece mascarado (`***.***.***-12`); na edição aparece completo.
- Um tutor que **possui pets** não pode ser excluído: exclua ou transfira os pets antes.

## 6. Pets
- Cadastre nome, espécie (Cão, Gato…), raça, idade em anos e escolha o **tutor** (cadastre o tutor antes).
- Um pet com **agendamentos** não pode ser excluído.

## 7. Agendamentos
1. *+ Novo agendamento* → escolha o **pet**, o **serviço** (Banho, Tosa, Banho e Tosa), a **data/hora** e o **status**.
2. Regras: não é permitido **data no passado** nem **dois agendamentos no mesmo horário** (agendamentos *cancelados* não ocupam horário).
3. Status: **Agendado → Em andamento → Concluído** ou **Cancelado**. Para mudar, use *Editar*.
4. Busca pelo **nome do pet**.

## 8. Produtos
- Cadastre nome, preço (aceita `25,90` ou `25.90`) e estoque (inteiro ≥ 0).
- Estoque **abaixo de 5** exibe o selo amarelo **baixo**.

## 9. Dúvidas frequentes
| Pergunta | Resposta |
|----------|----------|
| Esqueci a senha | Não há recuperação nesta versão. O responsável técnico deve apagar a pasta `data/` (**apaga todos os dados**) ou redefinir o hash no banco. |
| Posso buscar por CPF? | Não: CPF é criptografado por segurança. Busque pelo nome. |
| Perdi o arquivo `config/chave.key` | Os CPFs/telefones gravados não podem mais ser lidos. Mantenha backup da chave. |
| Como faço backup? | Com o sistema parado, copie a pasta `data/` **e** `config/chave.key` (em locais separados). |
