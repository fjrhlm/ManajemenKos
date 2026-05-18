import { supabase } from '@/lib/supabase';
import { NextResponse } from 'next/server';

// Kelola penghuni - atur kamar + buat tagihan
export async function POST(request, { params }) {
  try {
    const { id } = await params;
    const { nomor_kamar, bulan_tahun, nominal } = await request.json();

    // 1. Proses Kamar
    if (nomor_kamar) {
      let { data: kamar } = await supabase.from('kamar').select('*').eq('nomor_kamar', nomor_kamar).single();
      if (!kamar) {
        const { data: newKamar, error } = await supabase.from('kamar').insert({
          nomor_kamar, fasilitas: 'Standard', harga: nominal || 1000000, status: 'Terisi'
        }).select().single();
        if (error) throw error;
        kamar = newKamar;
      }
      await supabase.from('users').update({ id_kamar: kamar.id_kamar }).eq('id', id);
    }

    // 2. Proses Tagihan
    if (nominal) {
      const bulan = bulan_tahun || new Date().toLocaleDateString('en-US', { month: 'long', year: 'numeric' });
      const { error } = await supabase.from('tagihan').insert({
        id_user: id, bulan_tahun: bulan, nominal, status_bayar: 'Belum Bayar'
      });
      if (error) throw error;
    }

    return NextResponse.json({ status: 'success', message: 'Penghuni berhasil dikelola' });
  } catch (e) {
    return NextResponse.json({ status: 'error', message: e.message }, { status: 500 });
  }
}

// Hapus penghuni
export async function DELETE(request, { params }) {
  try {
    const { id } = await params;
    await supabase.from('tagihan').delete().eq('id_user', id);
    await supabase.from('keluhan').delete().eq('id_user', id);
    await supabase.from('users').delete().eq('id', id);
    return NextResponse.json({ status: 'success', message: 'Penghuni berhasil dihapus' });
  } catch (e) {
    return NextResponse.json({ status: 'error', message: e.message }, { status: 500 });
  }
}
