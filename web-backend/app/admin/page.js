'use client';
import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';

export default function AdminDashboard() {
  const [tab, setTab] = useState('tagihan');
  const [tagihan, setTagihan] = useState([]);
  const [penghuni, setPenghuni] = useState([]);
  const [msg, setMsg] = useState('');
  const [modal, setModal] = useState(null);
  const router = useRouter();

  useEffect(() => {
    if (typeof window !== 'undefined' && !localStorage.getItem('admin_logged_in')) {
      router.push('/admin/login');
      return;
    }
    loadData();
  }, []);

  const loadData = async () => {
    const [tRes, pRes] = await Promise.all([
      fetch('/api/admin/data/tagihan').then(r => r.json()),
      fetch('/api/admin/data/penghuni').then(r => r.json())
    ]);
    setTagihan(tRes.data || []);
    setPenghuni(pRes.data || []);
  };

  const handleValidasi = async (id) => {
    await fetch(`/api/admin/validasi/${id}`, { method: 'POST' });
    setMsg('Pembayaran berhasil divalidasi!');
    loadData();
  };

  const handleDeleteTagihan = async (id) => {
    if (!confirm('Yakin hapus tagihan ini?')) return;
    await fetch(`/api/admin/tagihan/${id}`, { method: 'DELETE' });
    setMsg('Tagihan berhasil dihapus!');
    loadData();
  };

  const handleDeletePenghuni = async (id, nama) => {
    if (!confirm(`Yakin hapus ${nama}? Semua data terkait akan ikut terhapus!`)) return;
    await fetch(`/api/admin/penghuni/${id}`, { method: 'DELETE' });
    setMsg(`Penghuni ${nama} berhasil dihapus!`);
    loadData();
  };

  const handleKelola = async (e, id) => {
    e.preventDefault();
    const form = new FormData(e.target);
    await fetch(`/api/admin/penghuni/${id}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        nomor_kamar: form.get('nomor_kamar'),
        bulan_tahun: form.get('bulan_tahun'),
        nominal: form.get('nominal')
      })
    });
    setModal(null);
    setMsg('Penghuni berhasil dikelola!');
    loadData();
  };

  const handleGenerate = async () => {
    if (!confirm('Generate tagihan bulan ini untuk semua anak kos?')) return;
    const res = await fetch('/api/admin/generate-tagihan', { method: 'POST' });
    const data = await res.json();
    setMsg(data.message);
    loadData();
  };

  const logout = () => { localStorage.removeItem('admin_logged_in'); router.push('/admin/login'); };

  const bulanIni = new Date().toLocaleDateString('en-US', { month: 'long', year: 'numeric' });

  return (
    <div style={{ minHeight: '100vh', background: '#0f172a', color: '#e2e8f0' }}>
      {/* Navbar */}
      <nav style={{ background: 'linear-gradient(135deg, #1e293b, #334155)', padding: '16px 30px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid rgba(255,255,255,0.1)' }}>
        <h1 style={{ fontSize: '20px', fontWeight: '700', margin: 0 }}>🏠 Panel Admin SiKos</h1>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button onClick={() => router.push('/admin/keluhan')} style={{ padding: '8px 16px', borderRadius: '8px', border: '1px solid #3b82f6', background: 'transparent', color: '#3b82f6', cursor: 'pointer', fontWeight: '600', fontSize: '13px' }}>Keluhan</button>
          <button onClick={logout} style={{ padding: '8px 16px', borderRadius: '8px', border: 'none', background: '#ef4444', color: '#fff', cursor: 'pointer', fontWeight: '600', fontSize: '13px' }}>Logout</button>
        </div>
      </nav>

      <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '24px' }}>
        {msg && <div style={{ background: '#22c55e', color: '#fff', padding: '12px 20px', borderRadius: '10px', marginBottom: '20px', fontWeight: '600', fontSize: '14px' }}>{msg} <span style={{ cursor: 'pointer', float: 'right' }} onClick={() => setMsg('')}>✕</span></div>}

        {/* Tabs */}
        <div style={{ display: 'flex', gap: '8px', marginBottom: '24px' }}>
          <button onClick={() => setTab('tagihan')} style={{ padding: '12px 24px', borderRadius: '10px', border: 'none', background: tab === 'tagihan' ? 'linear-gradient(135deg, #3b82f6, #8b5cf6)' : 'rgba(255,255,255,0.08)', color: '#fff', cursor: 'pointer', fontWeight: '600', fontSize: '14px' }}>🔥 Daftar Tagihan & Pembayaran</button>
          <button onClick={() => setTab('kelola')} style={{ padding: '12px 24px', borderRadius: '10px', border: 'none', background: tab === 'kelola' ? 'linear-gradient(135deg, #3b82f6, #8b5cf6)' : 'rgba(255,255,255,0.08)', color: '#fff', cursor: 'pointer', fontWeight: '600', fontSize: '14px' }}>👥 Kelola Kamar & Tagihan Anak Kos</button>
        </div>

        {/* TAB 1: Tagihan */}
        {tab === 'tagihan' && (() => {
          // Kelompokkan tagihan berdasarkan bulan_tahun
          const grouped = {};
          tagihan.forEach(t => {
            const key = t.bulan_tahun || 'Tidak Diketahui';
            if (!grouped[key]) grouped[key] = [];
            grouped[key].push(t);
          });
          const months = Object.keys(grouped);

          return (
          <div style={{ background: 'rgba(255,255,255,0.05)', borderRadius: '16px', border: '1px solid rgba(255,255,255,0.1)', overflow: 'hidden' }}>
            <div style={{ padding: '20px 24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid rgba(255,255,255,0.08)' }}>
              <h2 style={{ margin: 0, fontSize: '18px', fontWeight: '700' }}>Daftar Tagihan & Pembayaran</h2>
              <button onClick={handleGenerate} style={{ padding: '10px 20px', borderRadius: '10px', border: 'none', background: 'linear-gradient(135deg, #22c55e, #16a34a)', color: '#fff', cursor: 'pointer', fontWeight: '600', fontSize: '13px' }}>📅 Generate Tagihan Bulan Ini</button>
            </div>
            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                  <tr style={{ background: 'rgba(255,255,255,0.05)' }}>
                    {['No', 'Nama', 'Kamar', 'Nominal', 'Status', 'Aksi'].map(h => (
                      <th key={h} style={{ padding: '14px 16px', textAlign: 'center', fontSize: '13px', fontWeight: '600', color: '#94a3b8' }}>{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {months.length === 0 && <tr><td colSpan={6} style={{ padding: '40px', textAlign: 'center', color: '#64748b' }}>Belum ada data tagihan</td></tr>}
                  {months.map(bulan => (
                    <>
                      {/* Header Pemisah Bulan */}
                      <tr key={`header-${bulan}`}>
                        <td colSpan={6} style={{ padding: '14px 20px', background: 'linear-gradient(135deg, rgba(59,130,246,0.15), rgba(139,92,246,0.15))', borderTop: '2px solid rgba(59,130,246,0.3)', fontSize: '15px', fontWeight: '700', color: '#93c5fd' }}>
                          📅 {bulan}
                          <span style={{ marginLeft: '12px', fontSize: '12px', fontWeight: '500', color: '#64748b' }}>({grouped[bulan].length} tagihan)</span>
                        </td>
                      </tr>
                      {/* Data per Bulan */}
                      {grouped[bulan].map((t, i) => (
                        <tr key={t.id_tagihan} style={{ borderTop: '1px solid rgba(255,255,255,0.06)' }}>
                          <td style={{ padding: '14px 16px', textAlign: 'center', fontSize: '14px' }}>{i + 1}</td>
                          <td style={{ padding: '14px 16px', textAlign: 'center', fontWeight: '600', fontSize: '14px' }}>{t.nama || '-'}</td>
                          <td style={{ padding: '14px 16px', textAlign: 'center', fontSize: '14px' }}>{t.nomor_kamar || '-'}</td>
                          <td style={{ padding: '14px 16px', textAlign: 'center', fontSize: '14px' }}>Rp {Number(t.nominal).toLocaleString('id-ID')}</td>
                          <td style={{ padding: '14px 16px', textAlign: 'center' }}>
                            <span style={{ padding: '4px 12px', borderRadius: '20px', fontSize: '12px', fontWeight: '600', background: t.status_bayar === 'Lunas' ? '#22c55e' : t.status_bayar === 'Menunggu Validasi' ? '#f59e0b' : '#ef4444', color: '#fff' }}>{t.status_bayar}</span>
                          </td>
                          <td style={{ padding: '14px 16px', textAlign: 'center' }}>
                            <div style={{ display: 'flex', gap: '6px', justifyContent: 'center' }}>
                              {t.status_bayar !== 'Lunas' && <button onClick={() => handleValidasi(t.id_tagihan)} style={{ padding: '6px 14px', borderRadius: '8px', border: 'none', background: '#3b82f6', color: '#fff', cursor: 'pointer', fontSize: '12px', fontWeight: '600' }}>ACC</button>}
                              <button onClick={() => handleDeleteTagihan(t.id_tagihan)} style={{ padding: '6px 10px', borderRadius: '8px', border: '1px solid #ef4444', background: 'transparent', color: '#ef4444', cursor: 'pointer', fontSize: '12px' }}>🗑️</button>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
          );
        })()}

        {/* TAB 2: Kelola */}
        {tab === 'kelola' && (
          <div style={{ background: 'rgba(255,255,255,0.05)', borderRadius: '16px', border: '1px solid rgba(255,255,255,0.1)', overflow: 'hidden' }}>
            <div style={{ padding: '20px 24px', borderBottom: '1px solid rgba(255,255,255,0.08)' }}>
              <h2 style={{ margin: 0, fontSize: '18px', fontWeight: '700' }}>Kelola Kamar & Tagihan Anak Kos</h2>
            </div>
            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                  <tr style={{ background: 'rgba(255,255,255,0.05)' }}>
                    {['No', 'Nama', 'Email', 'Kamar', 'Aksi'].map(h => (
                      <th key={h} style={{ padding: '14px 16px', textAlign: 'center', fontSize: '13px', fontWeight: '600', color: '#94a3b8' }}>{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {penghuni.map((p, i) => (
                    <tr key={p.id} style={{ borderTop: '1px solid rgba(255,255,255,0.06)' }}>
                      <td style={{ padding: '14px 16px', textAlign: 'center', fontSize: '14px' }}>{i + 1}</td>
                      <td style={{ padding: '14px 16px', textAlign: 'center', fontWeight: '600', fontSize: '14px' }}>{p.nama}</td>
                      <td style={{ padding: '14px 16px', textAlign: 'center', fontSize: '14px', color: '#94a3b8' }}>{p.email}</td>
                      <td style={{ padding: '14px 16px', textAlign: 'center' }}>
                        <span style={{ padding: '4px 14px', borderRadius: '20px', fontSize: '12px', fontWeight: '600', background: p.nomor_kamar ? '#3b82f6' : '#64748b', color: '#fff' }}>{p.nomor_kamar ? `Kamar ${p.nomor_kamar}` : 'Belum Ada'}</span>
                      </td>
                      <td style={{ padding: '14px 16px', textAlign: 'center' }}>
                        <div style={{ display: 'flex', gap: '6px', justifyContent: 'center' }}>
                          <button onClick={() => setModal(p)} style={{ padding: '6px 14px', borderRadius: '8px', border: 'none', background: '#3b82f6', color: '#fff', cursor: 'pointer', fontSize: '12px', fontWeight: '600' }}>⚙️ Kelola</button>
                          <button onClick={() => handleDeletePenghuni(p.id, p.nama)} style={{ padding: '6px 10px', borderRadius: '8px', border: '1px solid #ef4444', background: 'transparent', color: '#ef4444', cursor: 'pointer', fontSize: '12px' }}>🗑️</button>
                        </div>
                      </td>
                    </tr>
                  ))}
                  {penghuni.length === 0 && <tr><td colSpan={5} style={{ padding: '40px', textAlign: 'center', color: '#64748b' }}>Belum ada penghuni</td></tr>}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>

      {/* Modal Kelola */}
      {modal && (
        <div style={{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.7)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 50 }}>
          <div style={{ background: '#1e293b', borderRadius: '16px', padding: '30px', width: '100%', maxWidth: '420px', border: '1px solid rgba(255,255,255,0.1)' }}>
            <h3 style={{ margin: '0 0 20px', fontSize: '18px', fontWeight: '700' }}>⚙️ Kelola - {modal.nama}</h3>
            <form onSubmit={(e) => handleKelola(e, modal.id)}>
              <div style={{ marginBottom: '16px' }}>
                <label style={{ color: '#3b82f6', fontSize: '13px', fontWeight: '700', display: 'block', marginBottom: '6px' }}>🔑 Nomor Kamar</label>
                <input name="nomor_kamar" defaultValue={modal.nomor_kamar || ''} placeholder="Contoh: 03" required
                  style={{ width: '100%', padding: '10px 14px', borderRadius: '8px', border: '1px solid rgba(255,255,255,0.15)', background: 'rgba(255,255,255,0.08)', color: '#fff', fontSize: '14px', boxSizing: 'border-box' }} />
              </div>
              <div style={{ marginBottom: '16px' }}>
                <label style={{ color: '#f59e0b', fontSize: '13px', fontWeight: '700', display: 'block', marginBottom: '6px' }}>📅 Bulan Tagihan</label>
                <input name="bulan_tahun" defaultValue={bulanIni} placeholder="Contoh: May 2026"
                  style={{ width: '100%', padding: '10px 14px', borderRadius: '8px', border: '1px solid rgba(255,255,255,0.15)', background: 'rgba(255,255,255,0.08)', color: '#fff', fontSize: '14px', boxSizing: 'border-box' }} />
              </div>
              <div style={{ marginBottom: '24px' }}>
                <label style={{ color: '#22c55e', fontSize: '13px', fontWeight: '700', display: 'block', marginBottom: '6px' }}>💵 Nominal Tagihan</label>
                <input name="nominal" type="number" defaultValue={modal.harga || ''} placeholder="Contoh: 1500000"
                  style={{ width: '100%', padding: '10px 14px', borderRadius: '8px', border: '1px solid rgba(255,255,255,0.15)', background: 'rgba(255,255,255,0.08)', color: '#fff', fontSize: '14px', boxSizing: 'border-box' }} />
                <span style={{ color: '#64748b', fontSize: '12px' }}>Kosongkan jika tidak ingin buat tagihan baru</span>
              </div>
              <div style={{ display: 'flex', gap: '10px' }}>
                <button type="button" onClick={() => setModal(null)} style={{ flex: 1, padding: '12px', borderRadius: '10px', border: '1px solid rgba(255,255,255,0.2)', background: 'transparent', color: '#94a3b8', cursor: 'pointer', fontWeight: '600' }}>Batal</button>
                <button type="submit" style={{ flex: 1, padding: '12px', borderRadius: '10px', border: 'none', background: 'linear-gradient(135deg, #3b82f6, #8b5cf6)', color: '#fff', cursor: 'pointer', fontWeight: '700' }}>Simpan</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
