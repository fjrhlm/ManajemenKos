import { supabase } from '@/lib/supabase';
import { NextResponse } from 'next/server';

export async function POST() {
  try {
    const bulanIni = new Date().toLocaleDateString('en-US', { month: 'long', year: 'numeric' });

    const { data: penghuni } = await supabase
      .from('users').select('id, id_kamar').eq('role', 'penghuni').not('id_kamar', 'is', null);

    let jumlahBaru = 0, sudahAda = 0;

    for (const p of penghuni || []) {
      const { data: existing } = await supabase.from('tagihan')
        .select('id_tagihan').eq('id_user', p.id).eq('bulan_tahun', bulanIni).single();

      if (!existing) {
        const { data: kamar } = await supabase.from('kamar').select('harga').eq('id_kamar', p.id_kamar).single();
        await supabase.from('tagihan').insert({
          id_user: p.id, bulan_tahun: bulanIni, nominal: kamar?.harga || 1000000, status_bayar: 'Belum Bayar'
        });
        jumlahBaru++;
      } else { sudahAda++; }
    }

    return NextResponse.json({
      status: 'success',
      message: `Tagihan ${bulanIni}: ${jumlahBaru} baru dibuat, ${sudahAda} sudah ada`
    });
  } catch (e) {
    return NextResponse.json({ status: 'error', message: e.message }, { status: 500 });
  }
}
