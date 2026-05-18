import { supabase } from '@/lib/supabase';
import { NextResponse } from 'next/server';

export async function GET() {
  try {
    const { data: tagihan } = await supabase.from('tagihan').select('*').order('id_tagihan', { ascending: false });
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
    return NextResponse.json({ data: result });
  } catch (e) {
    return NextResponse.json({ data: [], error: e.message });
  }
}
