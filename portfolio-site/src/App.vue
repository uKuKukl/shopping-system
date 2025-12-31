<template>
  <div class="page">
    <header class="nav">
      <div class="logo">Northline</div>
      <nav class="nav-links">
        <a href="#work">Work</a>
        <a href="#product">Product</a>
        <a href="#pricing">Pricing</a>
        <a href="#faq">FAQ</a>
      </nav>
      <a class="button ghost" href="#cta">Let’s talk</a>
    </header>

    <main class="shell">
      <section class="hero" id="top">
        <div class="hero-copy">
          <span class="badge">Product studio · SaaS growth</span>
          <h1>Launch bold products, faster.</h1>
          <p>
            I design and build conversion-first SaaS experiences. From concept to launch,
            I deliver landing pages, dashboards, and product visuals that make people click.
          </p>
          <div class="hero-actions">
            <a class="button" href="#pricing">Start a project</a>
            <a class="button ghost" href="#work">See portfolio</a>
          </div>
          <div class="hero-stats">
            <div>
              <strong>+68%</strong>
              <span>avg. lift in signups</span>
            </div>
            <div>
              <strong>12</strong>
              <span>launched SaaS rollouts</span>
            </div>
            <div>
              <strong>24h</strong>
              <span>first design draft</span>
            </div>
          </div>
        </div>
        <div class="hero-panel">
          <div class="glow"></div>
          <div class="hero-card">
            <div class="hc-top">
              <div class="spark"></div>
              <div>
                <p class="eyebrow">Realtime conversions</p>
                <h3>4.8k signups</h3>
                <small>+12.4% WoW</small>
              </div>
            </div>
            <div class="hc-chart">
              <div v-for="(bar, i) in chart" :key="i" class="bar" :style="{ height: bar + '%' }" />
            </div>
            <div class="hc-footer">
              <div>
                <p class="eyebrow">Launch window</p>
                <strong>3 weeks</strong>
              </div>
              <div>
                <p class="eyebrow">Stack</p>
                <strong>Vue · Vite · Figma</strong>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="section" id="product">
        <div class="section-title">
          <span class="badge">Product services</span>
          <h2>Full-stack landing pages with strategy baked in.</h2>
          <p>
            I pair brand direction, UX, and front-end craft to ship pages that look sharp and sell harder.
            Everything you need to move from idea to a ready-to-run launch.
          </p>
        </div>
        <div class="grid grid-3">
          <article v-for="item in features" :key="item.title" class="card feature">
            <div class="feature-icon">{{ item.icon }}</div>
            <h3>{{ item.title }}</h3>
            <p>{{ item.text }}</p>
            <ul>
              <li v-for="point in item.points" :key="point">{{ point }}</li>
            </ul>
          </article>
        </div>
      </section>

      <section class="section" id="work">
        <div class="section-title">
          <span class="badge">Selected work</span>
          <h2>Recent launches crafted for SaaS teams.</h2>
          <p>
            A few highlights spanning AI tooling, fintech dashboards, and subscription platforms. Each shipped with optimized copy, responsive UI, and launch assets.
          </p>
        </div>
        <div class="grid grid-2 work-grid">
          <article v-for="shot in portfolio" :key="shot.title" class="card work-card">
            <div class="tag">{{ shot.tag }}</div>
            <h3>{{ shot.title }}</h3>
            <p>{{ shot.text }}</p>
            <div class="chips">
              <span v-for="chip in shot.chips" :key="chip" class="chip">{{ chip }}</span>
            </div>
          </article>
        </div>
      </section>

      <section class="section pricing" id="pricing">
        <div class="section-title">
          <span class="badge">Engagements</span>
          <h2>Pick the lane that fits your launch.</h2>
          <p>Every plan includes design, build, QA, and basic analytics setup. I work async-first with crisp updates.</p>
        </div>
        <div class="grid grid-3">
          <article v-for="tier in pricing" :key="tier.name" class="card price-card" :class="{ hot: tier.hot }">
            <div class="pc-head">
              <div class="pc-name">{{ tier.name }}</div>
              <div class="pc-price">{{ tier.price }}</div>
              <p class="pc-desc">{{ tier.desc }}</p>
            </div>
            <ul>
              <li v-for="item in tier.items" :key="item">{{ item }}</li>
            </ul>
            <a class="button" href="#cta">Book this</a>
          </article>
        </div>
      </section>

      <section class="section" id="testimonials">
        <div class="section-title">
          <span class="badge">Testimonials</span>
          <h2>Teams trust the speed and polish.</h2>
          <p>Founder-led teams, product managers, and marketing leads working together to ship faster.</p>
        </div>
        <div class="grid grid-2">
          <article v-for="note in testimonials" :key="note.author" class="card quote">
            <p class="quote-body">“{{ note.body }}”</p>
            <div class="quote-meta">
              <strong>{{ note.author }}</strong>
              <span>{{ note.role }}</span>
            </div>
          </article>
        </div>
      </section>

      <section class="section cta" id="cta">
        <div class="cta-inner card">
          <div>
            <p class="eyebrow">Availability</p>
            <h2>Two new projects in January.</h2>
            <p class="muted">Tell me about your launch, and I’ll reply within one business day.</p>
          </div>
          <form class="cta-form" @submit.prevent>
            <div class="grid">
              <label>
                <span>Name</span>
                <input type="text" placeholder="Your name" required />
              </label>
              <label>
                <span>Work email</span>
                <input type="email" placeholder="you@company.com" required />
              </label>
            </div>
            <label>
              <span>Project goal</span>
              <textarea rows="3" placeholder="Launch a new landing page that converts for our AI product"></textarea>
            </label>
            <div class="cta-actions">
              <button type="submit" class="button">Send the brief</button>
              <span class="muted">Or email hello@northline.studio</span>
            </div>
          </form>
        </div>
      </section>

      <section class="section faq" id="faq">
        <div class="section-title">
          <span class="badge">FAQs</span>
          <h2>What you might be wondering.</h2>
        </div>
        <div class="grid">
          <details v-for="faq in faqs" :key="faq.q" class="card detail">
            <summary>{{ faq.q }}</summary>
            <p>{{ faq.a }}</p>
          </details>
        </div>
      </section>
    </main>

    <footer class="footer">
      <div>Northline Product Studio — crafted with Vue & Vite.</div>
      <div class="footer-links">
        <a href="#top">Back to top</a>
        <a href="#cta">Start a project</a>
      </div>
    </footer>
  </div>
</template>

<script setup>
const chart = [32, 52, 68, 44, 80, 64, 90, 72, 96, 84];

const features = [
  {
    title: 'Narrative-first hero design',
    text: 'Positioning, headline, and visual hierarchy tuned for the 5-second test.',
    icon: '⬢',
    points: ['Message-market fit audit', 'Hero, social proof, CTA stack', 'Mobile-first responsive layout'],
  },
  {
    title: 'Conversion systems',
    text: 'Pricing, FAQs, comparison tables, and objection handling built to convert.',
    icon: '◆',
    points: ['Pricing strategy & layout', 'Expandable FAQs with SEO schema-ready copy', 'Speed and accessibility pass'],
  },
  {
    title: 'Production build & handoff',
    text: 'Vite + Vue build, analytics, and tidy handoff that your team can own.',
    icon: '▣',
    points: ['Clean components with docs', 'Analytics & event tags wired', 'Launch support and QA sweep'],
  },
];

const portfolio = [
  {
    title: 'SignalIQ — AI ops dashboard',
    text: 'Landing page + product shots that grew waitlist by 72% in two weeks.',
    tag: 'AI SaaS',
    chips: ['Dashboard visuals', 'Story-driven hero', 'Vue static build'],
  },
  {
    title: 'Ledgerly — Fintech billing suite',
    text: 'Pricing page and onboarding flow tuned for clarity and trust.',
    tag: 'Fintech',
    chips: ['Pricing strategy', 'Trust elements', 'Speed index 98'],
  },
  {
    title: 'Orbit — API-first CRM',
    text: 'Developer-focused landing with code snippets and API changelog highlights.',
    tag: 'B2B SaaS',
    chips: ['API storytelling', 'Docs preview', 'Semantic markup'],
  },
  {
    title: 'Canvas — Collaborative canvas',
    text: 'Interactive visuals and motion to explain multiplayer workflows.',
    tag: 'Collab',
    chips: ['Hero motion', 'Responsive grid', 'Microcopy'],
  },
];

const pricing = [
  {
    name: 'Launch Sprint',
    price: '$3.2k',
    desc: 'One landing page, copy polish, and analytics wiring in 7-10 days.',
    items: ['Hero, features, pricing, FAQ', 'Responsive build in Vue + Vite', 'Basic GA + events', 'Two revision rounds'],
  },
  {
    name: 'Growth Bundle',
    price: '$5.6k',
    desc: 'Multi-section site, extra visuals, and test variants for campaigns.',
    hot: true,
    items: ['Everything in Launch Sprint', '2x additional pages/sections', 'A/B-ready variants', 'Launch support + QA'],
  },
  {
    name: 'Embedded',
    price: '$3k / wk',
    desc: 'Weekly product design/dev support for teams that move fast.',
    items: ['Design + front-end pairing', 'Backlog shaping and audits', 'Up to 20 hrs / week', 'Async daily updates'],
  },
];

const testimonials = [
  {
    author: 'Amelia Boyd',
    role: 'VP Product, Finch',
    body: 'We shipped a new pricing page in 9 days and doubled trial starts. Crisp communication and clean build.',
  },
  {
    author: 'Carlos Nunez',
    role: 'Founder, SignalIQ',
    body: 'He translated our messy beta into a story that resonated. The visuals and copy made fundraising easier.',
  },
  {
    author: 'Priya Desai',
    role: 'Growth Lead, Ledgerly',
    body: 'Fast iterations, thoughtful questions, and a codebase our team can maintain. Exactly what we needed.',
  },
  {
    author: 'Noah Keller',
    role: 'Marketing, Orbit',
    body: 'The developer-first tone was on point. Page speed is excellent and the layout is future-proof.',
  },
];

const faqs = [
  { q: 'How do we kick off?', a: 'Share your goal, audience, and must-haves. I’ll outline scope, price, and a timeline in 24 hours.' },
  { q: 'Do you handle copy?', a: 'Yes. I refine your positioning, headlines, and FAQ/objection handling to fit the design.' },
  { q: 'What tech do you use?', a: 'Vite + Vue for front-end, Figma for design. I can integrate analytics and basic marketing tags.' },
  { q: 'Can you work with my team?', a: 'Absolutely. I work async-first, join weekly standups if needed, and keep changes documented.' },
];
</script>

<style scoped>
.page {
  color: var(--text);
  background: transparent;
}

.shell {
  width: min(1200px, 92vw);
  margin: 0 auto;
  padding: 60px 0 120px;
  display: flex;
  flex-direction: column;
  gap: 90px;
}

.nav {
  position: sticky;
  top: 0;
  z-index: 10;
  width: min(1200px, 92vw);
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 18px 0;
  background: rgba(8, 12, 18, 0.82);
  backdrop-filter: blur(12px);
}

.logo {
  font-weight: 700;
  letter-spacing: -0.01em;
  font-size: 20px;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 18px;
}

.nav-links a {
  color: var(--muted);
  font-weight: 600;
}

.nav-links a:hover {
  color: var(--text);
}

.button.ghost {
  background: transparent;
  border-color: var(--stroke);
  box-shadow: none;
}

.hero {
  display: grid;
  gap: 32px;
  grid-template-columns: 1fr;
  align-items: center;
}

.hero-copy h1 {
  font-size: clamp(38px, 6vw, 54px);
  letter-spacing: -0.04em;
  line-height: 1.05;
}

.hero-copy p {
  color: var(--muted);
  line-height: 1.8;
  margin: 16px 0 24px;
  max-width: 640px;
}

.hero-actions {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
  margin-bottom: 22px;
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(120px, 1fr));
  gap: 16px;
}

.hero-stats strong {
  display: block;
  font-size: 22px;
}

.hero-stats span {
  color: var(--muted);
  font-size: 14px;
}

.hero-panel {
  position: relative;
}

.glow {
  position: absolute;
  inset: -30px;
  background: radial-gradient(circle, rgba(45, 212, 191, 0.08), transparent 50%);
  filter: blur(12px);
}

.hero-card {
  position: relative;
  background: var(--panel-strong);
  border: 1px solid var(--stroke);
  border-radius: 22px;
  padding: 24px;
  box-shadow: var(--shadow);
  overflow: hidden;
}

.hc-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.spark {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--accent-strong), var(--accent));
  filter: drop-shadow(0 0 16px rgba(249, 115, 22, 0.65));
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.16em;
  color: var(--muted);
  font-size: 12px;
}

.hc-chart {
  display: grid;
  grid-template-columns: repeat(10, 1fr);
  align-items: end;
  gap: 8px;
  height: 160px;
  margin: 22px 0;
}

.bar {
  background: linear-gradient(180deg, rgba(45, 212, 191, 0.85), rgba(45, 212, 191, 0.45));
  border-radius: 8px 8px 4px 4px;
}

.hc-footer {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  color: var(--muted);
  font-size: 14px;
}

.section {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.feature h3 {
  margin: 10px 0;
  font-size: 20px;
}

.feature p {
  color: var(--muted);
  line-height: 1.6;
  margin-bottom: 10px;
}

.feature-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  background: var(--panel-strong);
  font-size: 18px;
}

.feature ul {
  list-style: none;
  display: grid;
  gap: 8px;
  color: var(--muted);
}

.work-grid {
  gap: 18px;
}

.work-card {
  border: 1px solid var(--stroke);
  background: var(--panel);
}

.work-card .tag {
  display: inline-flex;
  padding: 8px 12px;
  border-radius: 12px;
  background: var(--panel-strong);
  color: var(--accent);
  font-weight: 700;
  margin-bottom: 10px;
}

.work-card h3 {
  margin-bottom: 10px;
}

.work-card p {
  color: var(--muted);
  line-height: 1.6;
  margin-bottom: 12px;
}

.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chip {
  padding: 8px 12px;
  border-radius: 12px;
  background: var(--panel-strong);
  color: var(--muted);
  font-size: 14px;
}

.pricing .grid {
  align-items: stretch;
}

.price-card {
  display: flex;
  flex-direction: column;
  gap: 16px;
  border: 1px solid var(--stroke);
}

.price-card.hot {
  border-color: rgba(249, 115, 22, 0.6);
  box-shadow: 0 16px 36px rgba(249, 115, 22, 0.2);
}

.pc-name {
  font-weight: 700;
  font-size: 18px;
}

.pc-price {
  font-size: 28px;
  margin: 8px 0;
}

.pc-desc {
  color: var(--muted);
  line-height: 1.5;
}

.price-card ul {
  list-style: none;
  display: grid;
  gap: 10px;
  color: var(--muted);
}

.quote {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.quote-body {
  font-size: 18px;
  line-height: 1.7;
}

.quote-meta {
  color: var(--muted);
  font-size: 14px;
}

.cta {
  margin-top: 20px;
}

.cta-inner {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
  border: 1px solid var(--stroke);
}

.cta-form {
  display: grid;
  gap: 12px;
}

.cta-form label {
  display: grid;
  gap: 6px;
  color: var(--muted);
  font-size: 14px;
}

.cta-form input,
.cta-form textarea {
  width: 100%;
  padding: 12px 14px;
  border-radius: 12px;
  border: 1px solid var(--stroke);
  background: rgba(255, 255, 255, 0.02);
  color: var(--text);
  font-size: 15px;
}

.cta-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.muted {
  color: var(--muted);
}

.detail summary {
  cursor: pointer;
  font-weight: 700;
}

.detail p {
  color: var(--muted);
  margin-top: 10px;
  line-height: 1.6;
}

.footer {
  width: min(1200px, 92vw);
  margin: 32px auto 48px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: var(--muted);
  font-size: 14px;
}

.footer-links {
  display: flex;
  gap: 16px;
}

@media (min-width: 960px) {
  .hero {
    grid-template-columns: 1.1fr 0.9fr;
  }

  .cta-inner {
    grid-template-columns: 1.1fr 1fr;
    align-items: center;
  }
}
</style>
