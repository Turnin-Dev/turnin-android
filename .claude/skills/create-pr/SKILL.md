---
name: create-pr
description: Open a GitHub PR from the current branch into develop, with the PK JIRA key in the title and the Korean PR template filled in.
disable-model-invocation: true
---

Arguments (optional): $ARGUMENTS — extra notes for the PR description, or a PK ticket key.

1. Check `git status` and the current branch. Refuse if on `develop` or `main`. If there are uncommitted changes, ask whether to commit them first (Korean Conventional Commit message including the PK key).
2. Find the PK key: from $ARGUMENTS, else the branch name (`<type>/PK-<n>-...`). If none, ask the user.
3. Review the changes with `git log develop..HEAD` and `git diff develop...HEAD`.
4. Push with `git push -u origin HEAD` if the branch has no upstream.
5. Create the PR with `gh pr create --base develop`:
   - Title: Conventional Commit style in Korean, containing the PK key (e.g. `[PK-153] feat: 로그인/회원가입 분석 이벤트 추가`).
   - Body: fill in `.github/pull_request_template.md` (keep its sections and checklist; write 작업 내용 in Korean, based on the diff). Leave the 스크린샷 section for the user if there are UI changes. End with:
     🤖 Generated with [Claude Code](https://claude.com/claude-code)
6. Print the PR URL. Remind the user to add assignees and labels (the template asks for them).

Never target `main`.
