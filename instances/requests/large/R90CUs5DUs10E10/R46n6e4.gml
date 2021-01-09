graph [
  node [
    id 0
    label "0"
    type 2
    prc 5
  ]
  node [
    id 1
    label "1"
    type 2
    prc 5
  ]
  node [
    id 2
    label "2"
    type 1
    prc 2
    ant 4
    prb 3
    x 91
    y 94
  ]
  node [
    id 3
    label "3"
    type 1
    prc 4
    ant 8
    prb 4
    x 96
    y 108
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 8
    prb 3
    x 21
    y 15
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 2
    prb 5
    x 54
    y 116
  ]
  edge [
    source 0
    target 5
    bandwith 262
    delay 338
  ]
  edge [
    source 0
    target 2
    bandwith 647
    delay 137
  ]
  edge [
    source 1
    target 3
    bandwith 474
    delay 259
  ]
  edge [
    source 1
    target 4
    bandwith 178
    delay 484
  ]
]
