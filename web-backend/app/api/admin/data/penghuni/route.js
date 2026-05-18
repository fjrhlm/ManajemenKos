import { supabase } from '@/lib/supabase';
import { NextResponse } from 'next/server';

export async function GET() {
  try {
    const { data: users } = await supabase.from('users').select('*').eq('role', 'penghuni').order('id', { ascending: true });
    const result = [];
    for (const u of users || []) {
      let nomor_kamar = null, harga = null;
      if (u.id_kamar) {
        const { data: kamar } = await supabase.from('kamar').select('nomor_kamar, harga').eq('id_kamar', u.id_kamar).single();
        nomor_kamar = kamar?.nomor_kamar;
        harga = kamar?.harga;
      }
      result.push({ id: u.id, nama: u.nama, email: u.email, nomor_kamar, harga });
    }
    return NextResponse.json({ data: result });
  } catch (e) {
    return NextResponse.json({ data: [], error: e.message });
  }
}
