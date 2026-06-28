import { supabase } from '@/lib/supabase';
import { NextResponse } from 'next/server';

export async function GET() {
  try {
    const { data: tagihan } = await supabase.from('tagihan').select('*').order('bulan_tahun', { ascending: false }).order('id_tagihan', { ascending: false });
    const result = [];
    for (const t of tagihan || []) {
      const { data: user } = await supabase.from('users').select('nama, id_kamar').eq('id', t.id_user).single();
      let nomor_kamar = null;
      if (user?.id_kamar) {
        const { data: kamar } = await supabase.from('kamar').select('nomor_kamar').eq('id_kamar', user.id_kamar).single();
        nomor_kamar = kamar?.nomor_kamar;
      }
      result.push({ ...t, nama: user?.nama, nomor_kamar });
    }

    // Sort by bulan_tahun secara kronologis (terbaru dulu)
    const monthOrder = ['January','February','March','April','May','June','July','August','September','October','November','December'];
    result.sort((a, b) => {
      const [mA, yA] = (a.bulan_tahun || '').split(' ');
      const [mB, yB] = (b.bulan_tahun || '').split(' ');
      const dateA = (parseInt(yA) || 0) * 12 + (monthOrder.indexOf(mA) || 0);
      const dateB = (parseInt(yB) || 0) * 12 + (monthOrder.indexOf(mB) || 0);
      if (dateB !== dateA) return dateB - dateA; // Terbaru dulu
      return (a.nama || '').localeCompare(b.nama || ''); // Lalu A-Z per nama
    });

    return NextResponse.json({ data: result });
  } catch (e) {
    return NextResponse.json({ data: [], error: e.message });
  }
}
