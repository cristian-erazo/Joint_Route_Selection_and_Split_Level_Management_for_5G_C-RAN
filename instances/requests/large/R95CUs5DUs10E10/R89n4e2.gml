graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
  ]
  node [
    id 1
    label "1"
    type 2
    prc 4
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 2
    prb 5
    x 56
    y 36
  ]
  node [
    id 3
    label "3"
    type 1
    prc 5
    ant 3
    prb 3
    x 37
    y 45
  ]
  edge [
    source 0
    target 2
    bandwith 523
    delay 120
  ]
  edge [
    source 1
    target 3
    bandwith 529
    delay 189
  ]
]
