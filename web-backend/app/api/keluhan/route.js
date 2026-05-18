import { supabase } from '@/lib/supabase';
import { parseBody } from '@/lib/parseBody';
import { NextResponse } from 'next/server';

// POST - Kirim keluhan baru
export async function POST(request) {
  try {
    const { id_user, judul, isi } = await parseBody(request);
    const { data, error } = await supabase.from('keluhan').insert({
      id_user, judul, isi
    }).select().single();
    if (error) throw error;
    return NextResponse.json({ status: 'success', message: 'Keluhan berhasil dikirim', data });
  } catch (e) {
    return NextResponse.json({ status: 'error', message: e.message }, { status: 500 });
  }
}
