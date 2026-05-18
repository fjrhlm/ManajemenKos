'use client';
import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';

export default function AdminKeluhan() {
  const [keluhan, setKeluhan] = useState([]);
  const [msg, setMsg] = useState('');
  const router = useRouter();

  useEffect(() => {
    if (typeof window !== 'undefined' && !localStorage.getItem('admin_logged_in')) { router.push('/admin/login'); return; }
    loadKeluhan();
  }, []);

  const loadKeluhan = async () => {
    const res = await fetch('/api/admin/data/keluhan');
    const data = await res.json();
    setKeluhan(data.data || []);
  };

  const handleDelete = async (id) => {
    if (!confirm('Yakin hapus keluhan ini?')) return;
    await fetch(`/api/admin/keluhan/${id}`, { method: 'DELETE' });
    setMsg('Keluhan berhasil dihapus!');
    loadKeluhan();
  };

  return (
    <div style={{ minHeight: '100vh', background: '#0f172a', color: '#e2e8f0' }}>
      <nav style={{ background: 'linear-gradient(135deg, #1e293b, #334155)', padding: '16px 30px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid rgba(255,255,255,0.1)' }}>
        <h1 style={{ fontSize: '20px', fontWeight: '700', margin: 0 }}>🏠 Panel Admin SiKos</h1>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button onClick={() => router.push('/admin')} style={{ padding: '8px 16px', borderRadius: '8px', border: '1px solid #3b82f6', background: 'transparent', color: '#3b82f6', cursor: 'pointer', fontWeight: '600', fontSize: '13px' }}>Dashboard</button>
          <button onClick={() => { localStorage.removeItem('admin_logged_in'); router.push('/admin/login'); }} style={{ padding: '8px 16px', borderRadius: '8px', border: 'none', background: '#ef4444', color: '#fff', cursor: 'pointer', fontWeight: '600', fontSize: '13px' }}>Logout</button>
        </div>
      </nav>
      <div style={{ maxWidth: '1000px', margin: '0 auto', padding: '24px' }}>
        {msg && <div style={{ background: '#22c55e', color: '#fff', padding: '12px 20px', borderRadius: '10px', marginBottom: '20px', fontWeight: '600', fontSize: '14px' }}>{msg}</div>}
        <div style={{ background: 'rgba(255,255,255,0.05)', borderRadius: '16px', border: '1px solid rgba(255,255,255,0.1)', overflow: 'hidden' }}>
          <div style={{ padding: '20px 24px', borderBottom: '1px solid rgba(255,255,255,0.08)' }}>
            <h2 style={{ margin: 0, fontSize: '18px', fontWeight: '700' }}>📋 Daftar Keluhan Penghuni</h2>
          </div>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead><tr style={{ background: 'rgba(255,255,255,0.05)' }}>
              {['No', 'Nama', 'Judul', 'Isi', 'Tanggal', 'Aksi'].map(h => <th key={h} style={{ padding: '14px 16px', textAlign: 'center', fontSize: '13px', fontWeight: '600', color: '#94a3b8' }}>{h}</th>)}
            </tr></thead>
            <tbody>
              {keluhan.map((k, i) => (
                <tr key={k.id_keluhan} style={{ borderTop: '1px solid rgba(255,255,255,0.06)' }}>
                  <td style={{ padding: '12px 16px', textAlign: 'center', fontSize: '14px' }}>{i + 1}</td>
                  <td style={{ padding: '12px 16px', textAlign: 'center', fontWeight: '600', fontSize: '14px' }}>{k.nama || '-'}</td>
                  <td style={{ padding: '12px 16px', textAlign: 'center', fontSize: '14px' }}>{k.judul}</td>
                  <td style={{ padding: '12px 16px', fontSize: '14px', maxWidth: '300px' }}>{k.isi}</td>
                  <td style={{ padding: '12px 16px', textAlign: 'center', fontSize: '12px', color: '#94a3b8' }}>{k.created_at}</td>
                  <td style={{ padding: '12px 16px', textAlign: 'center' }}>
                    <button onClick={() => handleDelete(k.id_keluhan)} style={{ padding: '6px 12px', borderRadius: '8px', border: '1px solid #ef4444', background: 'transparent', color: '#ef4444', cursor: 'pointer', fontSize: '12px' }}>🗑️ Hapus</button>
                  </td>
                </tr>
              ))}
              {keluhan.length === 0 && <tr><td colSpan={6} style={{ padding: '40px', textAlign: 'center', color: '#64748b' }}>Belum ada keluhan</td></tr>}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
