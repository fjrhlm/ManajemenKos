import { supabase } from '@/lib/supabase';
import { NextResponse } from 'next/server';
import bcrypt from 'bcryptjs';

export async function POST(request) {
  try {
    const { email, password } = await request.json();

    const { data: user, error } = await supabase
      .from('users')
      .select('*')
      .eq('email', email)
      .single();

    if (error || !user) {
      return NextResponse.json({ status: 'error', message: 'Email tidak ditemukan' }, { status: 401 });
    }

    const valid = await bcrypt.compare(password, user.password);
    if (!valid) {
      return NextResponse.json({ status: 'error', message: 'Password salah' }, { status: 401 });
    }

    return NextResponse.json({
      status: 'success',
      message: 'Login berhasil',
      data: {
        id_user: user.id,
        nama: user.nama,
        email: user.email,
        role: user.role
      }
    });
  } catch (e) {
    return NextResponse.json({ status: 'error', message: e.message }, { status: 500 });
  }
}
