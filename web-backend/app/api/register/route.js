import { supabase } from '@/lib/supabase';
import { parseBody } from '@/lib/parseBody';
import { NextResponse } from 'next/server';
import bcrypt from 'bcryptjs';

export async function POST(request) {
  try {
    const { nama, email, password } = await parseBody(request);

    const { data: existing } = await supabase.from('users').select('id').eq('email', email).single();
    if (existing) {
      return NextResponse.json({ status: 'error', message: 'Email sudah terdaftar' }, { status: 400 });
    }

    const hashedPassword = await bcrypt.hash(password, 12);

    const { data: user, error } = await supabase.from('users').insert({
      name: nama, nama, email, password: hashedPassword, role: 'penghuni'
    }).select().single();

    if (error) throw error;

    return NextResponse.json({
      status: 'success', message: 'Registrasi berhasil',
      data: { id_user: user.id, nama: user.nama, email: user.email, role: user.role }
    });
  } catch (e) {
    return NextResponse.json({ status: 'error', message: e.message }, { status: 500 });
  }
}
